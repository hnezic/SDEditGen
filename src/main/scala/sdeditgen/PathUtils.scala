package sdeditgen

import org.apache.commons.io.FilenameUtils
import java.nio.file.Paths
import ammonite.ops.Path

trait PathUtils
{
    def getPaths (sourcePathStr: String, relTargetFolder: String, sourceExt: String, targetExt: String) : (Path, Path) =
    {
        // Check file extension
        val prefix = FilenameUtils.getPrefix(sourcePathStr)
        val inFolder = FilenameUtils.getPath(sourcePathStr)
        val baseName = FilenameUtils.getBaseName(sourcePathStr)
        val extension = FilenameUtils.getExtension(sourcePathStr)

        if (! (extension equalsIgnoreCase sourceExt))
            throw new IllegalArgumentException (s"Input file must have '$sourceExt' extension!")

        // Set output path
        val targetName = baseName + "." + targetExt
        val targetPathStr = Paths.get(prefix, inFolder, relTargetFolder, targetName).toString
        (Path(sourcePathStr), Path(targetPathStr))
    }
}
