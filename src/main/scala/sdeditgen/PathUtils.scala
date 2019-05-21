package sdeditgen

import better.files.File
import ammonite.ops.Path

import scala.util.{Failure, Success, Try}

trait PathUtils
{
    def getPaths (sourceFileStr: String, targetFolderStr: String, sourceExt: String, targetExt: String) : Try[(Path, Path)] =
    {
        // First argument
        val absSourceFilePath = File(sourceFileStr)

        // Check if first argument is a file
        if (! absSourceFilePath.isRegularFile)
            return Failure(new IllegalArgumentException ("First argument is not a file!"))

        // Extract components
        val parent: File = absSourceFilePath.parent
        val nameWithoutExtension: String = absSourceFilePath.nameWithoutExtension
        val extension: Option[String] = absSourceFilePath.extension

        // Check if extension is correct
        if (! extension.exists(_ equalsIgnoreCase sourceExt) )
            return Failure(new IllegalArgumentException (s"Input file must have '$sourceExt' extension!"))

        val absTargetFolder: File = parent / targetFolderStr

        // Check if absolute target folder is a folder
        if (! absTargetFolder.isDirectory)
            return Failure(new IllegalArgumentException ("Second argument is not a folder!"))

        val targetFile: File = absTargetFolder / (nameWithoutExtension + targetExt)

        Success(Path(absSourceFilePath.pathAsString), Path(targetFile.pathAsString))
    }
}
