package sdeditgen

import better.files.File
import ammonite.ops.Path

trait PathUtils
{
    def getPaths (sourceFileStr: String, targetFolderStr: String, sourceExt: String, targetExt: String) : (Path, Path) =
    {
        // First argument
        val absSourceFilePath = File(sourceFileStr)

        // Check if first argument is a file
        if (! absSourceFilePath.isRegularFile)
            throw new IllegalArgumentException ("First argument is not a file!")

        // Extract components
        val parent: File = absSourceFilePath.parent
        val nameWithoutExtension: String = absSourceFilePath.nameWithoutExtension
        val extension: Option[String] = absSourceFilePath.extension

        // Check if extension is correct
        if (! extension.exists(_ equalsIgnoreCase sourceExt) )
            throw new IllegalArgumentException (s"Input file must have '$sourceExt' extension!")

        val absTargetFolder: File = parent / targetFolderStr

        // Check if absolute target folder is a folder
        if (! absTargetFolder.isDirectory)
            throw new IllegalArgumentException ("Second argument is not a folder!")

        val targetFile: File = absTargetFolder / (nameWithoutExtension + targetExt)

        (Path(absSourceFilePath.pathAsString), Path(targetFile.pathAsString))
    }
}
