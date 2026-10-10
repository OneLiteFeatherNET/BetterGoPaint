/*
 * goPaint is designed to simplify painting inside of Minecraft.
 * Copyright (C) Arcaniax-Development
 * Copyright (C) Arcaniax team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.onelitefeather.bettergopaint.docsgen;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Checks the GitBook pages under {@code docs/} without fetching anything. It reports:
 * <ol>
 *   <li>a relative link or image whose file does not exist (anchors and {@code scheme:} targets are not checked);</li>
 *   <li>a page under the "For builders" heading of {@code SUMMARY.md} that has neither an image nor a link to the
 *       screenshot shotlist;</li>
 *   <li>a file name in a {@code {% hint %}} block that starts with {@code images/} and is not in the shotlist.</li>
 * </ol>
 * Fenced code blocks are ignored.
 */
final class DocsLinkChecker {

    /**
     * Builder pages that have no illustration yet, relative to {@code docs}. The change {@code builder-guides-with-images}
     * (PR #47) embeds an image or a shotlist link in each of them and removes its entry here. A new builder page gets an
     * illustration in the change that adds it, so this list never grows.
     */
    static final Set<String> BUILDER_PAGES_AWAITING_ILLUSTRATIONS = Set.of("how-to/share-a-brush-as-an-item.md");

    private static final String SUMMARY = "SUMMARY.md";
    private static final String SHOTLIST = "development/screenshot-shotlist.md";
    private static final Pattern LINK = Pattern.compile("\\[[^\\]]*\\]\\(([^)\\s]+)(?:\\s+\"[^\"]*\")?\\)");
    private static final Pattern IMAGE = Pattern.compile("!\\[[^\\]]*\\]\\(");
    private static final Pattern SCHEME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*:");
    private static final Pattern BUILDERS_HEADING = Pattern.compile("^#{1,6}\\s+For builders\\s*$");
    private static final Pattern BUILDER_ENTRY = Pattern.compile("^\\s*[*-]\\s+\\[[^\\]]*\\]\\(([^)\\s]+)\\)");
    private static final Pattern HINT = Pattern.compile("\\{%\\s*hint\\b.*?%\\}(.*?)\\{%\\s*endhint\\s*%\\}", Pattern.DOTALL);
    private static final Pattern HINT_FILE = Pattern.compile("images/[A-Za-z0-9_./-]+");

    private final Path docs;
    private final Set<String> builderPagesWithoutIllustration;

    /**
     * @param docs                                the {@code docs} folder of the repository
     * @param builderPagesWithoutIllustration     builder pages, relative to {@code docs}, that may have neither an image
     *                                            nor a shotlist link
     */
    DocsLinkChecker(Path docs, Set<String> builderPagesWithoutIllustration) {
        this.docs = docs;
        this.builderPagesWithoutIllustration = Set.copyOf(builderPagesWithoutIllustration);
    }

    /**
     * @return one message per problem, pages in path order; empty when the docs pass
     */
    List<String> problems() {
        List<String> problems = new ArrayList<>();
        for (Path page : markdownPages()) {
            String name = relative(page);
            String text = withoutCodeFences(read(page));
            Matcher links = LINK.matcher(text);
            while (links.find()) {
                String target = links.group(1);
                Optional<Path> file = targetFile(page, target);
                if (file.isPresent() && !Files.isRegularFile(file.get())) {
                    problems.add(name + ": " + target + " does not resolve to a file");
                }
            }
            for (String token : hintFiles(text)) {
                if (!shotlist().contains(token)) {
                    problems.add(name + ": " + token + " is a planned image but is not listed in " + SHOTLIST);
                }
            }
        }
        problems.addAll(builderPageProblems());
        return problems;
    }

    private List<String> builderPageProblems() {
        List<String> problems = new ArrayList<>();
        for (String page : builderPages()) {
            if (builderPagesWithoutIllustration.contains(page)) {
                continue;
            }
            Path file = docs.resolve(page);
            if (!Files.isRegularFile(file)) {
                continue;
            }
            String text = withoutCodeFences(read(file));
            boolean hasImage = IMAGE.matcher(text).find();
            boolean linksShotlist = false;
            Matcher links = LINK.matcher(text);
            while (links.find()) {
                Optional<Path> target = targetFile(file, links.group(1));
                linksShotlist |= target.isPresent() && target.get().equals(docs.resolve(SHOTLIST).normalize());
            }
            if (!hasImage && !linksShotlist) {
                problems.add(page + ": builder page has neither an image nor a link to " + SHOTLIST);
            }
        }
        return problems;
    }

    /**
     * @return the pages listed under the "For builders" heading of {@code SUMMARY.md}, relative to {@code docs}
     */
    private List<String> builderPages() {
        Path summary = docs.resolve(SUMMARY);
        if (!Files.isRegularFile(summary)) {
            return List.of();
        }
        List<String> pages = new ArrayList<>();
        boolean inBuilders = false;
        for (String line : read(summary).split("\n", -1)) {
            if (line.startsWith("#")) {
                inBuilders = BUILDERS_HEADING.matcher(line).matches();
                continue;
            }
            Matcher entry = BUILDER_ENTRY.matcher(line);
            if (inBuilders && entry.find() && !SCHEME.matcher(entry.group(1)).find()) {
                pages.add(Path.of(stripAnchor(entry.group(1))).normalize().toString().replace('\\', '/'));
            }
        }
        return pages;
    }

    /**
     * @return the file names inside hint blocks that start with {@code images/}
     */
    private static List<String> hintFiles(String text) {
        List<String> files = new ArrayList<>();
        Matcher hint = HINT.matcher(text);
        while (hint.find()) {
            Matcher file = HINT_FILE.matcher(hint.group(1));
            while (file.find()) {
                files.add(file.group());
            }
        }
        return files;
    }

    private String shotlist() {
        Path file = docs.resolve(SHOTLIST);
        return Files.isRegularFile(file) ? read(file) : "";
    }

    /**
     * @return the file a link points to, or empty for an anchor on the same page or a target with a scheme
     */
    private Optional<Path> targetFile(Path page, String target) {
        String path = stripAnchor(target);
        if (path.isEmpty() || SCHEME.matcher(path).find() || path.startsWith("//")) {
            return Optional.empty();
        }
        return Optional.of(path.startsWith("/") ? docs.resolve(path.substring(1)).normalize()
                : page.getParent().resolve(path).normalize());
    }

    private static String stripAnchor(String target) {
        String path = target;
        if (path.startsWith("<") && path.endsWith(">")) {
            path = path.substring(1, path.length() - 1);
        }
        int cut = path.indexOf('#');
        return cut < 0 ? path : path.substring(0, cut);
    }

    private static String withoutCodeFences(String text) {
        StringBuilder kept = new StringBuilder();
        boolean fenced = false;
        for (String line : text.split("\n", -1)) {
            if (line.strip().startsWith("```")) {
                fenced = !fenced;
            } else if (!fenced) {
                kept.append(line).append('\n');
            }
        }
        return kept.toString();
    }

    private List<Path> markdownPages() {
        try (Stream<Path> paths = Files.walk(docs)) {
            return paths.filter(path -> path.toString().endsWith(".md") && Files.isRegularFile(path))
                    .sorted()
                    .toList();
        } catch (IOException exception) {
            throw new UncheckedIOException("could not list " + docs, exception);
        }
    }

    private String relative(Path page) {
        return docs.relativize(page).toString().replace('\\', '/');
    }

    private static String read(Path file) {
        try {
            return Files.readString(file, StandardCharsets.UTF_8).replace("\r\n", "\n");
        } catch (IOException exception) {
            throw new UncheckedIOException("could not read " + file, exception);
        }
    }
}
