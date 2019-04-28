package io.enezhrv.sdedit

import fastparse._

import org.scalatest.FunSuite
import org.scalatest.compatible.Assertion

abstract class UnitTest extends FunSuite with SDEditGenParser
{
    def parseCompareResult(input: String, expectedResult: String, rtrimResult: Boolean = false): Assertion =
    {
        val parseResult = parse(input, program(_))

        parseResult match
        {
            case Parsed.Success(ast, _) =>
                val result = ast.generate
                if (rtrimResult)
                    assert (result.rtrimLines == expectedResult)
                else
                    assert (result == expectedResult)

            case failure: Parsed.Failure => fail(failure.toString())
            case _ => fail
        }
    }
}
