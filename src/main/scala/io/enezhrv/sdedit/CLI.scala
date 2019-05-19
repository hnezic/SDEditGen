package io.enezhrv.sdedit

import ammonite.ops.{Path, read, write}
import fastparse._

object CLI extends SDEditGenParser with PathUtils
{
    def main(args: Array[String]): Unit =
    {
        // Get arguments
        if (args.length != 2)
            throw new IllegalArgumentException ("Usage: <input path> <relative output folder>")

        val (sourcePath, targetPath) = getPaths (args(0), args(1), "sdgen", "sd")
        val input = read! sourcePath

        val parseResult = parse(input, program(_))

        parseResult match
        {
            case Parsed.Success(ast, _) =>
                val output = ast.generate
                write.over (targetPath, output)

            case failure: Parsed.Failure => println(failure)
        }
    }
}
