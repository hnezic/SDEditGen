package io.enezhrv.sdedit

import fastparse._, NoWhitespace._

object Main extends App
{
    def objects[_: P] : P[List[ObjectDecl]] = P( (key("objects") ~ str("{") ~ objectDecl.rep(1) ~ str("}")).map(_.toList) )
    def objectDecl[_: P] : P[ObjectDecl] = P( (ident ~ ident ~ flag.rep.map(_.toSet))
                                               .map { case (name, type_, flags) => ObjectDecl(name, type_, flags) } )
    def flag[_: P] : P[ObjectFlag.Value] = P( key("named").map(_ => ObjectFlag.named) | key("existing").map(_ => ObjectFlag.existing) )

    def statements[_: P] : P[List[Statement]] = P( statement.rep.map(_.toList) )
    def statementsInBraces[_: P] : P[List[Statement]] = P( str("{") ~ statement.rep.map(_.toList) ~ str("}") )
    def statement[_: P] : P[Statement] = P( diagramLink )

    // Method, constructor, call
    def code[_: P] : P[Code] = P( (key("code") ~ ident ~ statementsInBraces).map{ case (obj, st) => new Code(obj, st) } )

    // Notes, comments
    def diagramLink[_: P] : P[DiagramLink] = P( (key("diagramLink") ~ string ~ ident.? ~ number ~ optEnd)
                                                 .map{ case (path, obj, num) => DiagramLink(path, obj, num) } )
    def title[_: P] : P[Title]= P( (key("title") ~ string ~ optEnd).map(t => Title(t)) )

    // Tokens
    def optEnd[_: P] : P[Unit] = P( (str(";") ~ white ).? )
    def ident[_: P] : P[String] = P( ( CharIn("a-zA-Z_").! ~ CharsWhileIn("a-zA-Z0-9_").rep.! )
                                    .map { case (first, rest) => first + rest }. filter( ! keywords.contains(_) ) ~ white )
    def string[_: P] : P[String] = P( "\"" ~/ CharsWhile(_ != '"').! ~ "\"" ~ white )
    def number[_: P] : P[Int] = P( CharIn("0-9").rep(1).!.map(_.toInt) ~ white )
    def key[_: P](s: String) : P[Unit] = P( s ~ white )
    def str[_: P](s: String) : P[Unit] = P( s ~ white )
    val keywords = Set( "objects", "named", "existing", "constructor", "method", "code", "create",
                        "call", "diagramLink", "fragment", "loop", "alt", "section", "title" )
    def white[_: P] : P[Unit] = P( CharIn(" \t\r\n").rep )

    println(
        parse(
            """diagramLink "this is my link"  123
              |diagramLink "another link" abc 4567 ;
            """.stripMargin, statements(_))
    )
}
