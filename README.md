# Black Rook OpenAL Utility

Copyright (c) 2014 Black Rook Software. All rights reserved.  
[http://blackrooksoftware.com/projects.htm?name=oalutil](http://blackrooksoftware.com/projects.htm?name=oalutil)  
[https://github.com/BlackRookSoftware/SoundOALUtil](https://github.com/BlackRookSoftware/SoundOALUtil)

### NOTICE

This library requires the use of third-party libraries. Black Rook Software 
is not responsible for issues regarding these libraries.

*This library is currently in **EXPERIMENTAL** status. This library's API
may change many times in different ways over the course of its development!* 

### Required Libraries

Black Rook Commons 2.16.0+  
[https://github.com/BlackRookSoftware/Common](https://github.com/BlackRookSoftware/Common)

Black Rook OpenAL  
[https://github.com/BlackRookSoftware/SoundOAL](https://github.com/BlackRookSoftware/SoundOAL)

### Introduction

The purpose of the Black Rook OpenAL Utility library is to extend the OpenAL
library's current function, outside of adding encapsulations: sound wave
generation and a simulated sound stage.

### Library

Contained herein is a set of classes for generating PCM data from abstract
waveforms, and a surrogate sound system used for playing back positional audio
in a three-dimensional environment. Using the latter, OpenAL doesn't even need to
be touched by the programmer at all, in most cases.

It is connected to the Java Sound Programming Interface in order to read 
multiple file formats and types. Support for additional file types can be 
added via adding additional Java Sound SPI-compatible decoders to the classpath.

### Other

This program and the accompanying materials are made available under the terms
of the GNU Lesser Public License v2.1 which accompanies this distribution, 
and is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

A copy of the LGPL should have been included in this release (LICENSE.txt).
If it was not, please contact us for a copy, or to notify us of a distribution
that has not included it. 
