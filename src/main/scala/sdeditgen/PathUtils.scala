package sdeditgen

import org.apache.commons.io.FilenameUtils
import java.nio.file.Paths
import ammonite.ops.Path

trait PathUtils
{
    def getPaths (sourcePathStr: String, relTargetFolder: String, sourceExt: String, targetExt: String) : (Path, Path) =
    {
        // sourcePathStr can be absolute or relative path
        // Convert it to absolute path
        val absSourcePathStr = Paths.get(sourcePathStr).toAbsolutePath.toString

        // Check file extension
        val prefix = FilenameUtils.getPrefix(absSourcePathStr)
        val inFolder = FilenameUtils.getPath(absSourcePathStr)
        val baseName = FilenameUtils.getBaseName(absSourcePathStr)
        val extension = FilenameUtils.getExtension(absSourcePathStr)

        if (! (extension equalsIgnoreCase sourceExt))
            throw new IllegalArgumentException (s"Input file must have '$sourceExt' extension!")

        // Set output path
        val targetName = baseName + "." + targetExt
        val targetPathStr = Paths.get(prefix, inFolder, relTargetFolder, targetName).toString
        (Path(absSourcePathStr), Path(targetPathStr))
    }
}
