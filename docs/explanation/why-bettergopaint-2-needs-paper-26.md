# Why BetterGoPaint 2 needs Paper 26 and Java 25

Paper 26 is built for Java 25, and so is the Paper API that BetterGoPaint is compiled against.
A plugin compiled against that API cannot run on a Java 21 server, which is what Minecraft 1.21
uses. Supporting both lines would mean building, testing and publishing two plugins from one
code base.

BetterGoPaint follows the newest stable Paper line instead. Servers on 1.21 keep a working plugin:
BetterGoPaint 1.1.x stays available on Hangar and Modrinth. It does not receive new features.

Paper versions that are only published as beta are not listed as supported. Hangar and Modrinth
would offer the plugin for them while their API can still change. A version is added once Paper
marks it stable.
