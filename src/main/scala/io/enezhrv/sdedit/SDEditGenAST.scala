package io.enezhrv.sdedit

case class Program(title: Option[Title], objects: List[ObjectDecl], statements: List[Statement])
{
    def generate: String =
    {
        val titleList = title.map(_.generate("")(0)).toList
        val objectsList = objects map (_.generate("")(0))
        val sepList = List("")
        val statementsList = statements map (_.generate("")(0))
        (titleList ++ objectsList ++ sepList ++ statementsList) mkString lineSep
    }
}

trait CodeGenerator
{
    def generate (parentName: String) (implicit level: Int): String

    def indent (str: String) (implicit level: Int) : String = {
        "    " * (level - 1) + str
    }
}

// -------------------------------------------------------------------------------------------

// OBJECTS

object ObjectFlag extends Enumeration
{
    type ObjectFlag = Value
    val named, existing = Value
}
import ObjectFlag._

case class ObjectDecl (name: String, objectType: String, flags: Set[ObjectFlag]) extends CodeGenerator
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        val prefix = if (flags contains ObjectFlag.existing) "" else "/"
        val suffix = if (flags contains ObjectFlag.named) "" else "[a]"
        indent (s"$prefix$name:$objectType$suffix") (level + 1)
    }
}

// -------------------------------------------------------------------------------------------

// STATEMENTS

sealed trait FragmentItem extends CodeGenerator

sealed trait Statement extends FragmentItem

// -------------------------------------------------------------------------------------------

// CALLS

abstract class Call (objectName: String, methodName: String, arguments: Arguments, result: Option[Result], optStatements: Option[List[Statement]]) extends Statement
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        val args = arguments.map(list => list.mkString("(", ", ", ")")).getOrElse("")
        val returned = result.map(_ + "=").getOrElse("")
        val part1 = indent (s"$parentName:$returned$objectName.$methodName$args")

        val optPart2 = optStatements . map { statements =>
            val list = statements map (_.generate(objectName)(level + 1))
            list mkString lineSep
        }

        List(Some(part1), optPart2) . collect { case Some(str) => str } . mkString(lineSep)
    }
}

class ObjectCreation (objectName: String, arguments: Arguments, result: Option[Result], optStatements: Option[List[Statement]])
    extends Call(objectName, "new", arguments, result, optStatements)

class MethodCall (objectName: String, methodName: String, arguments: Arguments, result: Option[Result], optStatements: Option[List[Statement]])
    extends Call(objectName, methodName, arguments, result, optStatements)

case class Argument (arg: String)
{
    override def toString: String = arg
}

case class Result (value: String)
{
    override def toString: String = value
}

case class StatementsInCall(statements: List[Statement])

// -------------------------------------------------------------------------------------------

// CONTAINERS

abstract class CodeContainer(objectName: String, statements: List[Statement]) extends Statement
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        val list = statements map (_.generate(objectName)(level + 1))
        list mkString lineSep
    }
}
class Constructor(objectName: String, statements: List[Statement]) extends CodeContainer(objectName, statements)
class Method(objectName: String, methodName: String, statements: List[Statement]) extends CodeContainer(objectName, statements)
class Code(objectName: String, statements: List[Statement]) extends CodeContainer(objectName, statements)

// -------------------------------------------------------------------------------------------

// FRAGMENTS

class Fragment(fragmentType: Option[String], common: FragmentCommon) extends Statement
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        val start = (fragmentType, common.text) match {
            case (Some(type_), Some(text)) => s"[c:$type_ $text]"
            case (Some(type_), None) => s"[c:$type_]"
            case (None, Some(text)) => s"[c:$text]"
            case (None, None) => s"[c]"
        }
        val startList = List(indent(start))
        val endList = List(indent("[/c]"))

        val middleList = common.items map (_.generate(parentName)(level + 1))

        val list = startList ++ middleList ++ endList
        list mkString lineSep
    }
}

class Loop(common: FragmentCommon) extends Fragment(Some("loop"), common)
class Alt(common: FragmentCommon) extends Fragment(Some("alt"), common)

case class FragmentCommon(text: Option[String], items: List[FragmentItem])

case class SectionDelimiter(text: Option[String]) extends FragmentItem
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        indent ("--[" + (text getOrElse "") + "]")
    }
}

// -------------------------------------------------------------------------------------------

case class DiagramLink(path: String, objectName: Option[String], idNumber: Int) extends Statement
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        val realObjectName = objectName getOrElse parentName

        val lines = List (
            indent (s"*$idNumber $realObjectName"),
            indent (s"link:$path") (level + 1),
            indent (s"*$idNumber")
        )

        lines mkString lineSep
    }
}

case class Title(text: String) extends Statement
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        s"#![$text]"
    }
}
