package sdeditgen

import ammonite.ops.{read, write}
import fastparse._

import scala.util.{Failure, Success}

object CLI extends SDEditGenParser with PathUtils
{
    def main(args: Array[String]): Unit =
    {
        // Get arguments
        if (args.length != 2)
            throw new IllegalArgumentException ("Usage: <input file> <output folder>")

        getPaths (args(0), args(1), ".sdgen", ".sd") match {
            case Success((sourcePath, targetPath)) =>
                val input = read! sourcePath

                val parseResult = parse(input, program(_))

                parseResult match
                {
                    case Parsed.Success(ast, _) =>
                        val output = ast.generate
                        write.over (targetPath, output)

                    case failure: Parsed.Failure => println(failure)
                }
            case Failure(e) => throw e
        }
    }
}
