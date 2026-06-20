package com.materials.core.util.share

class JvmShareManager : ShareManager {
    override fun sharePdf(filePath: String, title: String) {
        println("Sharing PDF on JVM: $filePath")
    }
}
