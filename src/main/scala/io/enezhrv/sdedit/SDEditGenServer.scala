package io.enezhrv.sdedit

import cask.model.Response
import fastparse._

object Routes extends cask.Routes with SDEditGenParser
{
    @cask.post("/generate")
    def generate(request: cask.Request) =
    {
        val input = new String(request.readAllBytes())

        val parseResult = parse(input, program(_))

        parseResult match
        {
            case Parsed.Success(ast, _) =>
                Response(ast.generate)

            case failure: Parsed.Failure =>
                Response(failure.toString, 400)
        }
    }

    initialize()
}

object SDEditGenServer extends cask.Main(Routes)