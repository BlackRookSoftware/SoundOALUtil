==============================================================================
Building this Project
Black Rook FileSystem Library
(C) Black Rook Software, All rights reserved.
http://www.blackrooksoftware.com
==============================================================================

This project is built via Apache Ant. You'll need to download Ant from here:
https://ant.apache.org/bindownload.cgi

The build script (build.xml) contains multiple targets of note, including:

clean 
	Cleans the build directory contents.
compile
	Compiles the Java source to classes.
javadoc
	Creates the Javadocs for this library.
jar
	JARs up the binaries, source, and docs into separate JARs.
zip
	ZIPs up the project contents into separate ZIP files (binaries, source, 
	and docs).
release
	Synonymous with "zip".

The build script also contains multiple properties of note, including:

build.version.number
	Version number of the build.
	Default: Current time formatted as "yyyy.MM.dd.HHmmssSSS".
build.version.appendix
	Type of build (usually "BUILD" or "RELEASE" or "STABLE" or "SNAPSHOT").
	Default: "SNAPSHOT".
dev.base
	The base directory for other projects that need including for the build.
	Default: ".."
build.dir
	The base directory for built resources.
	Default: "build"
common.lib
	The location of the Black Rook Commons Library binaries (for build 
	classpath).
	Default: "${dev.base}/Common/bin"
oal.lib
	The location of the Black Rook OpenAL Library binaries (for build 
	classpath).
	Default: "${dev.base}/SoundOAL/bin"
