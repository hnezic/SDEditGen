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

trait ASTNode
{
    def generate (parentName: String) (implicit level: Int): String

    def indent (str: String) (implicit level: Int) : String = {
        "    " * (level - 1) + str
    }
}

// -------------------------------------------------------------------------------------------

// OBJECTS

case class ObjectDecl (name: String, objectType: String, flags: Option[Map[String, Flag]]) extends ASTNode
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        val updatedFlags: List[Flag] = (ObjectDecl.defaultFlags ++ flags.getOrElse(Map[String, Flag]())).values.toList

        val prefix = updatedFlags.map(_.getPrefix).mkString
        val suffix = updatedFlags.map(_.getSuffix).mkString

        indent (s"$prefix$name:$objectType$suffix") (level + 1)
    }
}
object ObjectDecl
{
    val defaultFlags: Map[String, Flag] = Map ("named" -> Named(false), "existing" -> Existing(false))
}

// FLAGS

sealed trait Flag
{
    def getPrefix: String = ""
    def getSuffix: String = ""
}

case class Named(value: Boolean) extends Flag
{
    override def getSuffix: String = if (value) "" else "[a]"
}

case class Existing(value: Boolean) extends Flag
{
    override def getPrefix: String = if (value) "" else "/"
}

// -------------------------------------------------------------------------------------------

// STATEMENTS

sealed trait Statement extends ASTNode

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

case class Argument (exp: Expression)
{
    override def toString: String = exp.toString
}

case class Result (exp: Expression)
{
    override def toString: String = exp.toString
}

case class Expression (value: String)
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
class Object(objectName: String, statements: List[Statement]) extends CodeContainer(objectName, statements)

// -------------------------------------------------------------------------------------------

// FRAGMENTS

class Fragment(fragmentType: Option[String], body: FragmentBody) extends Statement
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        val start = (fragmentType, body.text) match {
            case (Some(type_), Some(text)) => s"[c:$type_ $text]"
            case (Some(type_), None) => s"[c:$type_]"
            case (None, Some(text)) => s"[c:$text]"
            case (None, None) => s"[c]"
        }
        val startList = List(indent(start))
        val endList = List(indent("[/c]"))

        val middleList = body.items map (_.generate(parentName)(level + 1))

        val list = startList ++ middleList ++ endList
        list mkString lineSep
    }
}

class Loop(body: FragmentBody) extends Fragment(Some("loop"), body)
class Alt(body: FragmentBody) extends Fragment(Some("alt"), body)

case class FragmentBody(text: Option[String], items: List[FragmentItem])

sealed trait FragmentItem extends ASTNode

case class StatementFragmentItem(statement: Statement) extends FragmentItem
{
    def generate (parentName: String) (implicit level: Int): String =
    {
        statement.generate(parentName)
    }
}

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
