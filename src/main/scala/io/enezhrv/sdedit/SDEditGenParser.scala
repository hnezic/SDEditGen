package io.enezhrv.sdedit

import fastparse._, NoWhitespace._

trait SDEditGenParser
{
    def program[_: P] : P[Program] = P( (white ~ title.? ~ objects ~ statements ~ End)
                                        . map { case (tit, obj, st) => Program(tit, obj, st) } )

    def objects[_: P] : P[List[ObjectDecl]] = P( (key("objects") ~/ str("{") ~ objectDecl.rep(1) ~ str("}")).map(_.toList) )
    def objectDecl[_: P] : P[ObjectDecl] = P( (ident ~ colon.? ~ ident ~ flag.rep.map(_.toSet))
                                            . map { case (name, type_, flags) => ObjectDecl(name, type_, flags) } )
    def flag[_: P] : P[ObjectFlag.Value] = P( key("named").map(_ => ObjectFlag.named) | key("existing").map(_ => ObjectFlag.existing) )

    def statements[_: P] : P[List[Statement]] = P( statement.rep.map(_.toList) )
    def statementsInBraces[_: P] : P[List[Statement]] = P( str("{") ~ statement.rep.map(_.toList) ~ str("}") )
    def statement[_: P] : P[Statement] = P( constructor | method | code | objectCreation | methodCall | diagramLink | fragment | loop | alt )

    // Method call, object creation
    def objectCreation[_: P] : P[ObjectCreation] = P( (key("create") ~/ ident ~ arguments.? ~ result.? ~ optStatements)
                                                        . map { case (obj, args, res, st) => new ObjectCreation(obj, args, res, st) } )
    def methodCall[_: P]: P[MethodCall] = P( (key("call") ~/ ident ~ dot.? ~ methodName ~ arguments.? ~ result.? ~ optStatements)
                                                        . map { case (obj, met, args, res, st) => new MethodCall(obj, met, args, res, st) } )
    def methodName[_: P] : P[String] = P( ident | string )
    def optStatements[_: P]: P[Option[List[Statement]]] = P( statementsInBraces.map(Option(_)) | optEnd.map (_ => None) )

    def arguments[_: P] : P[List[Argument]] = P( str("(") ~ argument.rep(sep=comma./).map(_.toList) ~ str(")") )
    def argument[_: P] : P[Argument] = P( ident . map(arg => Argument(arg)) )
    def result[_: P] : P[Result] = P( key("return") ~ ident . map(r => Result(r)) )

    // Method, constructor, call
    def constructor[_: P] : P[Constructor] = P( (key("constructor") ~/ ident ~ statementsInBraces)
                                                . map{ case (obj, st) => new Constructor(obj, st) } )
    def method[_: P]: P[Method] = P( (key("method") ~/ ident ~ methodName ~ statementsInBraces)
                                        . map { case (obj, met, st) => new Method (obj, met, st) } )
    def code[_: P] : P[Code] = P( (key("code") ~/ ident ~ statementsInBraces)
                                    . map{ case (obj, st) => new Code(obj, st) } )

    // Fragments
    def fragment[_: P] : P[Fragment] = P( (key("fragment") ~/ ident.? ~ fragmentCommon) . map { case (type_, common) => new Fragment(type_, common) } )
    def loop[_: P] : P[Loop] = P( key("loop") ~/ fragmentCommon . map(new Loop(_)) )
    def alt[_: P] : P[Alt] = P( key("alt") ~/ fragmentCommon . map(new Alt(_)) )

    def fragmentCommon[_: P] : P[FragmentCommon] = P( (string.? ~ fragmentItems) . map { case (text, items) => FragmentCommon(text, items) } )
    def fragmentItems[_: P] : P[List[FragmentItem]] = P( str("{") ~ fragmentItem.rep(1).map(_.toList) ~ str("}") )
    def fragmentItem[_: P] : P[FragmentItem] = P( statement | sectionDelimiter )
    def sectionDelimiter[_: P] : P[FragmentItem] = P( key("section") ~ string.? . map(SectionDelimiter) )

    // Notes, comments
    def diagramLink[_: P] : P[DiagramLink] = P( (key("diagramLink") ~/ string ~ ident.? ~ number ~ optEnd)
                                                .map{ case (path, obj, num) => DiagramLink(path, obj, num) } )
    def title[_: P] : P[Title]= P( (key("title") ~/ string ~ optEnd).map(t => Title(t)) )

    // Tokens
    def comma[_: P] : P[Unit] = P( str(",") )
    def dot[_: P] : P[Unit] = P( str(".") )
    def colon[_: P] : P[Unit] = P( str(":") )
    def optEnd[_: P] : P[Unit] = P( str(";").? )

    def ident[_: P] : P[String] = P( ( CharIn("a-zA-Z_").! ~ CharsWhileIn("a-zA-Z0-9_").rep.! )
        .map { case (first, rest) => first + rest }. filter( ! keywords.contains(_) ) ~ white )
    def string[_: P] : P[String] = P( "\"" ~/ CharsWhile(_ != '"').! ~ "\"" ~ white )
    def number[_: P] : P[Int] = P( CharIn("0-9").rep(1).!.map(_.toInt) ~ white )

    def key[_: P](s: String) : P[Unit] = P( s ~ white )
    def str[_: P](s: String) : P[Unit] = P( s ~ white )
    val keywords = Set( "objects", "named", "existing", "constructor", "method", "code", "create",
        "call", "diagramLink", "fragment", "loop", "alt", "section", "title" )

    def white[_: P] : P[Unit] = P( CharIn(" \t\r\n").rep )
}
