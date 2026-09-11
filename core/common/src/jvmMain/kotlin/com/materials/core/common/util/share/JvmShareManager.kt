package com.materials.core.common.util.share

class JvmShareManager : ShareManager {
    override fun sharePdf(filePath: String, title: String) {
        println("Sharing PDF on JVM: $filePath")
    }
}
