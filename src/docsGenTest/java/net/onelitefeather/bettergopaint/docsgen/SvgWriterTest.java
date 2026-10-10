package net.onelitefeather.bettergopaint.docsgen;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SvgWriterTest {

    @Test
    @DisplayName("an empty document is a root element with its size and a trailing newline")
    void emptyDocument() {
        String svg = SvgWriter.write(new SvgDocument(40, 20));

        assertThat(svg).as("the svg of an empty 40 by 20 document").isEqualTo(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"20\" viewBox=\"0 0 40 20\">\n"
                        + "</svg>\n");
    }
}
