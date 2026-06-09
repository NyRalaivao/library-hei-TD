package com.library.hei.file.hash;

import com.library.hei.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
