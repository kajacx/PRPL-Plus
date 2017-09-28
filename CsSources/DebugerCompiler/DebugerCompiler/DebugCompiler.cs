
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;

namespace DebugerCompiler
{
    public class DebugerCompiler
    {
        public static void Main(string[] args)
        {
            Thread.CurrentThread.CurrentCulture = System.Globalization.CultureInfo.InvariantCulture;

            //string toCompile = @"c:\Users\Karel\Documents\My Games\particlefleet\editor\PRPL Console\scripts\TextBind.prpl";
            //string toCompile = @"c:\Users\Karel\Documents\My Games\particlefleet\editor\CannonspawnTest\scripts\SpawnCannonShot.prpl";
            string toCompile = Console.ReadLine();

            string msg;
            Dictionary<string, CrplCore.Data> inputVar;
            List<CrplCore.Command> commands = commandsFromScript(toCompile, out msg, out inputVar);
            Console.WriteLine(msg);


            if (commands != null)
            {
                commands.RemoveAt(commands.Count - 1); //remove the EOL command

                Console.WriteLine("#Script variables");
                foreach (var scriptVar in inputVar)
                {
                    string data = scriptVar.Value.GetType() == CrplCore.Data.TYPE.INT ? scriptVar.Value.int_val.ToString() :
                        (scriptVar.Value.GetType() == CrplCore.Data.TYPE.FLOAT ? scriptVar.Value.float_val.ToString() :
                        '"' + scriptVar.Value.GetString() + '"');
                    Console.WriteLine("${0}:{1}", scriptVar.Key, data);
                }


                Console.WriteLine();
                Console.WriteLine("once");
                Console.WriteLine("#Register script variables");
                foreach (var scriptVar in inputVar)
                {
                    Console.WriteLine("<-invokeUsedVariables \"{0}\" AppendToList", scriptVar.Key);
                }


                Console.WriteLine();
                Console.WriteLine("#Source code lines");
                string[] lines = File.ReadAllLines(toCompile);
                for (int l = 0; l < lines.Length; l++)
                {
                    StringBuilder builder = new StringBuilder();
                    string[] words = lines[l].Split('"');
                    int i = 0;
                    foreach (var word in words)
                    {
                        if (i > 0)
                        {
                            builder.Append(" DoubleQuote Concat ");
                        }
                        builder.Append('"').Append(word).Append('"');
                        if (i > 0)
                        {
                            builder.Append(" Concat");
                        }
                        i++;
                    }
                    builder.Append(" ->debugger__line").Append(l + 1);

                    Console.WriteLine(builder.ToString());
                }
                Console.WriteLine("\"return\" ->debugger__line" + (lines.Length + 1));
                Console.WriteLine((lines.Length + 1) + " ->debugger__lines");


                Console.WriteLine();
                Console.WriteLine("#Commands init");
                for (int l = 1; l <= lines.Length + 1; l++)
                {
                    Console.WriteLine("CreateList ->debugger__commands" + l);
                }


                Console.WriteLine();
                Console.WriteLine("#Commands fill");
                foreach (var command in commands)
                {
                    Console.WriteLine("<-debugger__commands{0} {1} AppendToList", command.lineNumber, getCommandDisplayText(command));
                }
                Console.WriteLine("<-debugger__commands" + (lines.Length + 1) + " \"return\" AppendToList");


                Console.WriteLine("endonce");
                Console.WriteLine();
                Console.WriteLine("#Code execution");
                int lineNumber = -1;
                int index = 0;
                foreach (var command in commands)
                {
                    if (command.lineNumber > lineNumber)
                    {
                        lineNumber = command.lineNumber;
                        index = 0;
                    }
                    else
                    {
                        index = index + 1;
                    }

                    // line index type @invoke command 
                    Console.WriteLine("{0} {1} {2} @debuggerIntercept {3}", command.lineNumber, index, getTypeForCommand(command), getCommandExecuteText(command));
                }
                Console.WriteLine("{0} {1} {2} @debuggerIntercept <-debuggerCallStack dup GetListCount 1 sub RemoveListElement", lines.Length + 1, 0, 2);
            }
            else
            {
                Environment.Exit(1);
            }
        }

        public static bool isDebuggerInspect(CrplCore.Command command)
        {
            return command.statement == CrplCore.STATEMENT.DELETE && command.data.string_val.Equals("debuggerInspect", StringComparison.InvariantCultureIgnoreCase);
        }

        public static int getTypeForCommand(CrplCore.Command command)
        {
            switch (command.statement)
            {
                case CrplCore.STATEMENT.CALL: return 1; //function call
                case CrplCore.STATEMENT.FUNC: //function definition
                case CrplCore.STATEMENT.RETURN: return 2;
                default: return 0;
            }
        }

        public static string getCommandExecuteText(CrplCore.Command command)
        {
            switch (command.statement)
            {
                case CrplCore.STATEMENT.READ: return "<-" + command.data.string_val;
                case CrplCore.STATEMENT.WRITE: return "\"" + command.data.string_val + "\" @invokeRegisterVariable ->" + command.data.string_val;
                case CrplCore.STATEMENT.EXISTS: return "-?" + command.data.string_val;
                case CrplCore.STATEMENT.DELETE: return (isDebuggerInspect(command) ? "@" : "--") + command.data.string_val;

                case CrplCore.STATEMENT.REFREAD: return "<-!";
                case CrplCore.STATEMENT.REFWRITE: return "dup @invokeRegisterVariable ->!";
                case CrplCore.STATEMENT.REFEXISTS: return "-?!";
                case CrplCore.STATEMENT.REFDELETE: return "--!";

                case CrplCore.STATEMENT.PUSH:
                    switch (command.data.GetType())
                    {
                        case CrplCore.Data.TYPE.INT: return command.data.int_val.ToString();
                        case CrplCore.Data.TYPE.FLOAT: return command.data.float_val.ToString();
                        case CrplCore.Data.TYPE.STRING: return '"' + command.data.string_val.ToString() + '"';
                        default: throw new Exception("Invalid data type: " + command.data.GetType());
                    }

                case CrplCore.STATEMENT.RETURN: return "<-debuggerCallStack dup GetListCount 1 sub RemoveListElement return";
                case CrplCore.STATEMENT.FUNC: return "<-debuggerCallStack dup GetListCount 1 sub RemoveListElement :" + command.data.string_val; // FUNC = definition
                case CrplCore.STATEMENT.CALL: return "<-debuggerCallStack \"" + command.data.string_val + "\" AppendToList @" + command.data.string_val; // CALL = usage

                default: return (command.data.string_val != null && command.data.string_val.Length > 0) ? command.data.string_val : command.statement.ToString().ToLower();
            }
        }

        public static string getCommandDisplayText(CrplCore.Command command)
        {
            switch (command.statement)
            {
                case CrplCore.STATEMENT.READ: return "\"<-" + command.data.string_val + '"';
                case CrplCore.STATEMENT.WRITE: return "\"->" + command.data.string_val + '"';
                case CrplCore.STATEMENT.EXISTS: return "\"-?" + command.data.string_val + '"';
                case CrplCore.STATEMENT.DELETE: return (isDebuggerInspect(command) ? "\"@" : "\"--") + command.data.string_val + '"';

                case CrplCore.STATEMENT.REFREAD: return "\"<-!\"";
                case CrplCore.STATEMENT.REFWRITE: return "\"->!\"";
                case CrplCore.STATEMENT.REFEXISTS: return "\"-?!\"";
                case CrplCore.STATEMENT.REFDELETE: return "\"--!\"";

                case CrplCore.STATEMENT.PUSH:
                    switch (command.data.GetType())
                    {
                        case CrplCore.Data.TYPE.INT: return '"' + command.data.int_val.ToString() + '"';
                        case CrplCore.Data.TYPE.FLOAT: return '"' + command.data.float_val.ToString() + '"';
                        case CrplCore.Data.TYPE.STRING: return "DoubleQuote \"" + command.data.string_val.ToString() + "\" DoubleQuote Concat Concat";
                        default: throw new Exception("Invalid data type: " + command.data.GetType());
                    }

                case CrplCore.STATEMENT.FUNC: return "\":" + command.data.string_val + '"'; // FUNC = definition
                case CrplCore.STATEMENT.CALL: return "\"@" + command.data.string_val + '"'; // CALL = usage

                default: return '"' + ((command.data.string_val != null && command.data.string_val.Length > 0) ? command.data.string_val : command.statement.ToString().ToLower()) + '"';
            }
        }

        public static List<CrplCore.Command> commandsFromScript(string scriptName, out string message, out Dictionary<string, CrplCore.Data> inputVar)
        {
            List<CrplCore.Command> commands;
            Dictionary<string, int> funcTable;

            CrplCompiler compiler = new CrplCompiler();
            if (compiler.Compile(scriptName, File.ReadAllText(scriptName), out message, out commands, out funcTable, out inputVar, true))
            {
                return commands;
            }
            return null;
        }
    }

    public class CrplCompiler
    {
        private struct Token
        {
            public string tok;

            public int lineNumber;

            public Token(string tok, int lineNumber)
            {
                this.tok = tok;
                this.lineNumber = lineNumber;
            }
        }

        private List<CrplCore.Command> commands;

        private Dictionary<string, int> funcTable;

        private Dictionary<string, CrplCore.Data> inputVars;

        private string ReplaceNotInQuotes(string src, string what, string with)
        {
            string[] array = src.Split(new char[]
            {
            '"'
            });
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < array.Length; i++)
            {
                if (i % 2 == 0)
                {
                    stringBuilder.Append(array[i].Replace(what, with));
                }
                else
                {
                    stringBuilder.Append("\"");
                    stringBuilder.Append(array[i]);
                    stringBuilder.Append("\"");
                }
            }
            return stringBuilder.ToString();
        }

        public bool Compile(string scriptName, string program, out string resultMessage, out List<CrplCore.Command> commands, out Dictionary<string, int> funcTable, out Dictionary<string, CrplCore.Data> inputVars, bool overwriteCache = false)
        {
            commands = new List<CrplCore.Command>();
            funcTable = new Dictionary<string, int>();
            inputVars = new Dictionary<string, CrplCore.Data>();
            this.commands = commands;
            this.funcTable = funcTable;
            resultMessage = "Success!";
            string text = program.Replace("\r", string.Empty);
            text = text.Replace("\n", string.Empty);
            text = text.Trim();
            program = this.ReplaceNotInQuotes(program, "(", " ( ");
            program = this.ReplaceNotInQuotes(program, ")", " ) ");
            program = this.ReplaceNotInQuotes(program, "[", " [ ");
            program = this.ReplaceNotInQuotes(program, "]", " ] ");
            List<CrplCompiler.Token> list = CrplCompiler.SplitIntoTokens(program, new char[]
            {
            ' ',
            '\t',
            '\r',
            '\n'
            });
            foreach (CrplCompiler.Token current in list)
            {
                string text2 = current.tok.Trim();
                if (!(text2 == string.Empty))
                {
                    if (text2.StartsWith("$"))
                    {
                        string text3 = null;
                        int num = text2.IndexOf(":");
                        string text4;
                        if (num != -1)
                        {
                            text4 = text2.Substring(0, num);
                            if (num < text2.Length - 1)
                            {
                                text3 = text2.Substring(num + 1);
                            }
                        }
                        else
                        {
                            text4 = text2;
                        }
                        if (text4 != null && text4.Length == 1)
                        {
                            text4 = null;
                        }
                        if (text4 != null && text4.Length > 1)
                        {
                            text4 = text4.Substring(1);
                        }
                        if (text4 == null)
                        {
                            resultMessage = string.Concat(new object[]
                            {
                            "Line: ",
                            current.lineNumber,
                            ": Improperly formatted input var.  There must be a name for the var and a default value.: ",
                            text2
                            });
                            commands.Clear();
                            bool result = false;
                            return result;
                        }
                        if (inputVars.ContainsKey(text4))
                        {
                            resultMessage = string.Concat(new object[]
                            {
                            "Line: ",
                            current.lineNumber,
                            ": Duplicate input var declared: ",
                            text4
                            });
                            commands.Clear();
                            bool result = false;
                            return result;
                        }
                        if (text3 == null)
                        {
                            resultMessage = string.Concat(new object[]
                            {
                            "Line: ",
                            current.lineNumber,
                            ": An input var must have some default value: ",
                            text2
                            });
                            commands.Clear();
                            bool result = false;
                            return result;
                        }
                        CrplCore.Data value;
                        if (text3.StartsWith("\""))
                        {
                            if (!text2.EndsWith("\""))
                            {
                                resultMessage = string.Concat(new object[]
                                {
                                "Line: ",
                                current.lineNumber,
                                ": A string literal in an input var definition must have a quote at the beginning and at the end: ",
                                text3
                                });
                                commands.Clear();
                                bool result = false;
                                return result;
                            }
                            value = new CrplCore.Data(text3.Substring(1, text3.Length - 2));
                        }
                        else if (text3.Contains("."))
                        {
                            float val = 0f;
                            bool flag = float.TryParse(text3, out val);
                            if (!flag)
                            {
                                resultMessage = string.Concat(new object[]
                                {
                                "Line: ",
                                current.lineNumber,
                                ": In an input var a dot is only allowed in string literals and floating point numbers: ",
                                text3
                                });
                                commands.Clear();
                                bool result = false;
                                return result;
                            }
                            value = new CrplCore.Data(val);
                        }
                        else
                        {
                            int val2 = 0;
                            bool flag2 = int.TryParse(text3, out val2);
                            if (!flag2)
                            {
                                resultMessage = string.Concat(new object[]
                                {
                                "Line: ",
                                current.lineNumber,
                                ": Improperly formatted number in an input var: ",
                                text3
                                });
                                commands.Clear();
                                bool result = false;
                                return result;
                            }
                            value = new CrplCore.Data(val2);
                        }
                        inputVars[text4] = value;
                    }
                    else if (text2.StartsWith(":"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.FUNC, text2.Substring(1, text2.Length - 1), current.lineNumber);
                    }
                    else if (text2.StartsWith("@"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.CALL, text2.Substring(1, text2.Length - 1), current.lineNumber);
                    }
                    else if (text2.StartsWith("->!*"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.REFWRITEGLOBAL, current.lineNumber);
                    }
                    else if (text2.StartsWith("<-!*"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.REFREADGLOBAL, current.lineNumber);
                    }
                    else if (text2.StartsWith("-?!*"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.REFEXISTSGLOBAL, text2.Substring(4, text2.Length - 4), current.lineNumber);
                    }
                    else if (text2.StartsWith("--!*"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.REFDELETEGLOBAL, text2.Substring(4, text2.Length - 4), current.lineNumber);
                    }
                    else if (text2.StartsWith("->*"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.WRITEGLOBAL, text2.Substring(3, text2.Length - 3), current.lineNumber);
                    }
                    else if (text2.StartsWith("<-*"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.READGLOBAL, text2.Substring(3, text2.Length - 3), current.lineNumber);
                    }
                    else if (text2.StartsWith("-?*"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.EXISTSGLOBAL, text2.Substring(3, text2.Length - 3), current.lineNumber);
                    }
                    else if (text2.StartsWith("--*"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.DELETEGLOBAL, text2.Substring(3, text2.Length - 3), current.lineNumber);
                    }
                    else if (text2.StartsWith("->!"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.REFWRITE, current.lineNumber);
                    }
                    else if (text2.StartsWith("<-!"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.REFREAD, current.lineNumber);
                    }
                    else if (text2.StartsWith("-?!"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.REFEXISTS, text2.Substring(3, text2.Length - 3), current.lineNumber);
                    }
                    else if (text2.StartsWith("--!"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.REFDELETE, text2.Substring(3, text2.Length - 3), current.lineNumber);
                    }
                    else if (text2.StartsWith("->"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.WRITE, text2.Substring(2, text2.Length - 2), current.lineNumber);
                    }
                    else if (text2.StartsWith("<-"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.READ, text2.Substring(2, text2.Length - 2), current.lineNumber);
                    }
                    else if (text2.StartsWith("-?"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.EXISTS, text2.Substring(2, text2.Length - 2), current.lineNumber);
                    }
                    else if (text2.StartsWith("--"))
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.DELETE, text2.Substring(2, text2.Length - 2), current.lineNumber);
                    }
                    else if (text2.StartsWith("\""))
                    {
                        if (!text2.EndsWith("\""))
                        {
                            resultMessage = string.Concat(new object[]
                            {
                            "Line: ",
                            current.lineNumber,
                            ": A string literal must have a quote at the beginning and at the end: ",
                            text2
                            });
                            commands.Clear();
                            bool result = false;
                            return result;
                        }
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.PUSH, text2.Substring(1, text2.Length - 2), current.lineNumber);
                    }
                    else if (text2.Contains("."))
                    {
                        float arg = 0f;
                        bool flag3 = float.TryParse(text2, out arg);
                        if (!flag3)
                        {
                            resultMessage = string.Concat(new object[]
                            {
                            "Line: ",
                            current.lineNumber,
                            ": A dot is only allowed in string literals and floating point numbers: ",
                            text2
                            });
                            commands.Clear();
                            bool result = false;
                            return result;
                        }
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.PUSH, arg, current.lineNumber);
                    }
                    else if (text2 == "(")
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.OPENTRAN, current.lineNumber);
                    }
                    else if (text2 == ")")
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.CLOSETRAN, current.lineNumber);
                    }
                    else if (text2 == "[")
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.OPENBRACKET, current.lineNumber);
                    }
                    else if (text2 == "]")
                    {
                        CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.CLOSEBRACKET, current.lineNumber);
                    }
                    else
                    {
                        int arg2 = 0;
                        bool flag4 = int.TryParse(text2, out arg2);
                        if (flag4)
                        {
                            CrplCore.Command command = this.AppendCommand(CrplCore.STATEMENT.PUSH, arg2, current.lineNumber);
                        }
                        else
                        {
                            try
                            {
                                CrplCore.STATEMENT statement = (CrplCore.STATEMENT)((int)Enum.Parse(typeof(CrplCore.STATEMENT), text2.ToUpper()));
                                CrplCore.Command command = this.AppendCommand(statement, text2, current.lineNumber);
                            }
                            catch (Exception var_20_9E0)
                            {
                                if (text2.Contains(":"))
                                {
                                    resultMessage = string.Concat(new object[]
                                    {
                                    "Line: ",
                                    current.lineNumber,
                                    ": Invalid Token: ",
                                    text2,
                                    ". Perhaps you meant $",
                                    text2
                                    });
                                }
                                else
                                {
                                    resultMessage = string.Concat(new object[]
                                    {
                                    "Line: ",
                                    current.lineNumber,
                                    ": Invalid Token: ",
                                    text2,
                                    ". Perhaps you meant ->",
                                    text2,
                                    " or <-",
                                    text2,
                                    " or \"",
                                    text2,
                                    "\" or @",
                                    text2
                                    });
                                }
                                commands.Clear();
                                bool result = false;
                                return result;
                            }
                        }
                    }
                }
            }
            this.AppendCommand(CrplCore.STATEMENT.EOS, 0);
            string text5;
            if (!this.WarpValidateScript(out text5))
            {
                resultMessage = text5;
                commands.Clear();
                return false;
            }
            if (!this.BracketValidateScript(out text5))
            {
                resultMessage = text5;
                commands.Clear();
                return false;
            }
            this.HandleTranspositions();
            text5 = this.HandleListIndexes();
            if (text5 != null)
            {
                resultMessage = text5;
                commands.Clear();
                return false;
            }
            text5 = this.HandleProperties();
            if (text5 != null)
            {
                resultMessage = text5;
                commands.Clear();
                return false;
            }
            if (!this.ValidateScript(out text5))
            {
                resultMessage = text5;
                commands.Clear();
                return false;
            }
            if (!this.SecondPass(out text5))
            {
                resultMessage = text5;
                commands.Clear();
                return false;
            }
            if (!this.ValidateCalls(out text5))
            {
                resultMessage = text5;
                commands.Clear();
                return false;
            }
            return true;
        }

        private static List<CrplCompiler.Token> SplitIntoTokens(string stringToSplit, params char[] delimiters)
        {
            List<CrplCompiler.Token> list = new List<CrplCompiler.Token>();
            bool flag = false;
            bool flag2 = false;
            int num = 1;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < stringToSplit.Length; i++)
            {
                char c = stringToSplit[i];
                if (c == '#' && !flag)
                {
                    flag2 = true;
                }
                else if (c == '"' && !flag2)
                {
                    flag = !flag;
                    stringBuilder.Append(c);
                }
                else if (delimiters.Contains(c) && !flag && !flag2)
                {
                    string text = stringBuilder.ToString().Trim();
                    if (text != string.Empty)
                    {
                        list.Add(new CrplCompiler.Token(text, num));
                    }
                    stringBuilder = new StringBuilder();
                }
                else if (!flag2)
                {
                    stringBuilder.Append(c);
                }
                if (c == '\n')
                {
                    flag2 = false;
                }
                if (c == '\n')
                {
                    num++;
                }
            }
            string text2 = stringBuilder.ToString().Trim();
            if (text2 != string.Empty)
            {
                list.Add(new CrplCompiler.Token(text2, num));
            }
            return list;
        }

        private void HandleTranspositions()
        {
            int num = this.commands.Count;
            for (int i = 0; i < num; i++)
            {
                if (this.commands[i].statement == CrplCore.STATEMENT.OPENTRAN && i > 0)
                {
                    int index = this.FindCloseTran(i);
                    this.commands[index] = this.commands[i - 1];
                    this.commands.RemoveAt(i - 1);
                    this.commands.RemoveAt(i - 1);
                    num -= 2;
                    i--;
                }
            }
        }

        private string HandleProperties()
        {
            for (int i = 0; i < this.commands.Count; i++)
            {
                CrplCore.Command value = this.commands[i];
                if ((value.statement == CrplCore.STATEMENT.READ || value.statement == CrplCore.STATEMENT.READGLOBAL) && value.data.GetType() == CrplCore.Data.TYPE.STRING)
                {
                    string @string = value.data.GetString();
                    if (@string.Contains("."))
                    {
                        int num = @string.IndexOf('.');
                        value.data = new CrplCore.Data(@string.Substring(0, num));
                        this.commands[i] = value;
                        try
                        {
                            string text = "GET" + @string.Substring(num + 1, @string.Length - num - 1);
                            CrplCore.STATEMENT statement = (CrplCore.STATEMENT)((int)Enum.Parse(typeof(CrplCore.STATEMENT), text.ToUpper()));
                            CrplCore.Command item = new CrplCore.Command(statement, text, value.lineNumber);
                            this.commands.Insert(i + 1, item);
                        }
                        catch (Exception var_7_E9)
                        {
                            string result = string.Concat(new object[]
                            {
                            "Line: ",
                            value.lineNumber,
                            ": Invalid Property Token: ",
                            @string,
                            ". Perhaps you meant ->",
                            @string,
                            " or <-",
                            @string,
                            " or \"",
                            @string,
                            "\" or @",
                            @string
                            });
                            return result;
                        }
                    }
                }
                else if (i > 0 && (value.statement == CrplCore.STATEMENT.WRITE || value.statement == CrplCore.STATEMENT.WRITEGLOBAL) && value.data.GetType() == CrplCore.Data.TYPE.STRING)
                {
                    string string2 = value.data.GetString();
                    if (string2.Contains("."))
                    {
                        try
                        {
                            int lineNumber = this.commands[i].lineNumber;
                            int num2 = string2.IndexOf('.');
                            CrplCore.Command value2 = new CrplCore.Command(CrplCore.STATEMENT.READ, string2.Substring(0, num2), lineNumber);
                            CrplCore.Command item2 = new CrplCore.Command(CrplCore.STATEMENT.SWAP, null, lineNumber);
                            string text2 = "SET" + string2.Substring(num2 + 1, string2.Length - num2 - 1);
                            CrplCore.STATEMENT statement2 = (CrplCore.STATEMENT)((int)Enum.Parse(typeof(CrplCore.STATEMENT), text2.ToUpper()));
                            CrplCore.Command item3 = new CrplCore.Command(statement2, text2, value.lineNumber);
                            this.commands[i] = value2;
                            this.commands.Insert(i + 1, item2);
                            this.commands.Insert(i + 2, item3);
                        }
                        catch (Exception var_24_276)
                        {
                            string result = string.Concat(new object[]
                            {
                            "Line: ",
                            value.lineNumber,
                            ": Invalid Property Token: ",
                            string2,
                            ". Perhaps you meant ->",
                            string2,
                            " or <-",
                            string2,
                            " or \"",
                            string2,
                            "\" or @",
                            string2
                            });
                            return result;
                        }
                    }
                }
            }
            return null;
        }

        private string HandleListIndexes()
        {
            int num = this.commands.Count;
            for (int i = 0; i < num; i++)
            {
                if (this.commands[i].statement == CrplCore.STATEMENT.OPENBRACKET && i > 0)
                {
                    int index = this.FindCloseBracket(i);
                    CrplCore.Command value = this.commands[i - 1];
                    int lineNumber = this.commands[i].lineNumber;
                    if (value.statement == CrplCore.STATEMENT.WRITE || value.statement == CrplCore.STATEMENT.WRITEGLOBAL)
                    {
                        if (value.statement == CrplCore.STATEMENT.WRITE)
                        {
                            value.statement = CrplCore.STATEMENT.READ;
                        }
                        else if (value.statement == CrplCore.STATEMENT.WRITEGLOBAL)
                        {
                            value.statement = CrplCore.STATEMENT.READGLOBAL;
                        }
                        this.commands[i - 1] = value;
                        CrplCore.Command value2 = new CrplCore.Command(CrplCore.STATEMENT.SETLISTELEMENTRPN, lineNumber);
                        this.commands[index] = value2;
                        this.commands.RemoveAt(i);
                        num--;
                        i--;
                    }
                    else
                    {
                        CrplCore.Command value3 = new CrplCore.Command(CrplCore.STATEMENT.GETLISTELEMENT, lineNumber);
                        this.commands[index] = value3;
                        this.commands.RemoveAt(i);
                        num--;
                        i--;
                    }
                }
            }
            return null;
        }

        private bool SecondPass(out string message)
        {
            message = string.Empty;
            for (int i = 0; i < this.commands.Count; i++)
            {
                CrplCore.Command value = this.commands[i];
                if (value.statement == CrplCore.STATEMENT.IF || value.statement == CrplCore.STATEMENT.ELSE)
                {
                    value.data = new CrplCore.Data(this.FindJumpPoint(i));
                    this.commands[i] = value;
                }
                else if (value.statement == CrplCore.STATEMENT.DO)
                {
                    value.data = new CrplCore.Data(this.FindLoop(i));
                    this.commands[i] = value;
                }
                else if (value.statement == CrplCore.STATEMENT.ONCE)
                {
                    value.data = new CrplCore.Data(this.FindEndOnce(i));
                    this.commands[i] = value;
                }
                else if (value.statement == CrplCore.STATEMENT.REPEAT)
                {
                    value.data = new CrplCore.Data(this.FindEndWhile(i));
                    this.commands[i] = value;
                }
                else if (value.statement == CrplCore.STATEMENT.BREAK)
                {
                    value.data = new CrplCore.Data(this.FindDoOrEndWhile(i));
                    this.commands[i] = value;
                }
                else if (value.statement == CrplCore.STATEMENT.RETURN)
                {
                    int num = this.FindDoOrEndWhile(i);
                    if (num > 0)
                    {
                        value.data = new CrplCore.Data(num);
                        this.commands[i] = value;
                    }
                }
                else if (value.statement == CrplCore.STATEMENT.FUNC)
                {
                    string @string = value.data.GetString();
                    if (this.funcTable.ContainsKey(@string))
                    {
                        message = string.Concat(new object[]
                        {
                        "Line: ",
                        value.lineNumber,
                        " Duplicate Functions defined: ",
                        @string
                        });
                        return false;
                    }
                    this.funcTable[@string] = i;
                }
            }
            message = string.Empty;
            return true;
        }

        private bool ValidateCalls(out string message)
        {
            foreach (CrplCore.Command current in this.commands)
            {
                if (current.statement == CrplCore.STATEMENT.CALL && !this.funcTable.ContainsKey(current.data.GetString()))
                {
                    message = string.Concat(new object[]
                    {
                    "Line: ",
                    current.lineNumber,
                    " Calling a nonexistent function: ",
                    current.data.GetString()
                    });
                    return false;
                }
            }
            message = string.Empty;
            return true;
        }

        private bool WarpValidateScript(out string message)
        {
            int num = 0;
            Stack<int> stack = new Stack<int>();
            foreach (CrplCore.Command current in this.commands)
            {
                if (current.statement == CrplCore.STATEMENT.OPENTRAN)
                {
                    num++;
                    stack.Push(current.lineNumber);
                }
                else if (current.statement == CrplCore.STATEMENT.CLOSETRAN)
                {
                    num--;
                    if (num < 0)
                    {
                        message = "Line " + current.lineNumber + ": A ')' is missing a matching '('";
                        bool result = false;
                        return result;
                    }
                    stack.Pop();
                }
            }
            if (num > 0)
            {
                int num2 = -1;
                try
                {
                    num2 = stack.Peek();
                }
                catch (Exception)
                {
                }
                message = "Line " + num2 + ": An '(' is missing a matching ')'";
                return false;
            }
            message = string.Empty;
            return true;
        }

        private bool BracketValidateScript(out string message)
        {
            int num = 0;
            Stack<int> stack = new Stack<int>();
            foreach (CrplCore.Command current in this.commands)
            {
                if (current.statement == CrplCore.STATEMENT.OPENBRACKET)
                {
                    num++;
                    stack.Push(current.lineNumber);
                }
                else if (current.statement == CrplCore.STATEMENT.CLOSEBRACKET)
                {
                    num--;
                    if (num < 0)
                    {
                        message = "Line " + current.lineNumber + ": A ']' is missing a matching '['";
                        bool result = false;
                        return result;
                    }
                    stack.Pop();
                }
            }
            if (num > 0)
            {
                int num2 = -1;
                try
                {
                    num2 = stack.Peek();
                }
                catch (Exception)
                {
                }
                message = "Line " + num2 + ": An '[' is missing a matching ']'";
                return false;
            }
            message = string.Empty;
            return true;
        }

        private bool ValidateScript(out string message)
        {
            int num = 0;
            int num2 = 0;
            Stack<int> stack = new Stack<int>();
            int num3 = 0;
            int num4 = 0;
            Stack<int> stack2 = new Stack<int>();
            int num5 = 0;
            Stack<int> stack3 = new Stack<int>();
            int num6 = 0;
            Stack<int> stack4 = new Stack<int>();
            int num7 = 0;
            Stack<int> stack5 = new Stack<int>();
            int num8 = 0;
            foreach (CrplCore.Command current in this.commands)
            {
                if (current.statement == CrplCore.STATEMENT.READ || current.statement == CrplCore.STATEMENT.WRITE || current.statement == CrplCore.STATEMENT.EXISTS || current.statement == CrplCore.STATEMENT.FUNC || current.statement == CrplCore.STATEMENT.CALL)
                {
                    if (current.data.GetType() != CrplCore.Data.TYPE.NULL)
                    {
                        string @string = current.data.GetString();
                        if (!Regex.IsMatch(@string, "^[a-zA-Z0-9_\\\\.]+$"))
                        {
                            message = string.Concat(new object[]
                            {
                            "Line ",
                            current.lineNumber,
                            ": A variable or function name must be alphanumeric: ",
                            @string
                            });
                            bool result = false;
                            return result;
                        }
                    }
                }
                else if (current.statement == CrplCore.STATEMENT.OPENTRAN)
                {
                    num7++;
                    stack5.Push(current.lineNumber);
                }
                else if (current.statement == CrplCore.STATEMENT.CLOSETRAN)
                {
                    num7--;
                    if (num7 < 0)
                    {
                        message = "Line " + current.lineNumber + ": A ')' is missing a matching '('";
                        bool result = false;
                        return result;
                    }
                    stack5.Pop();
                }
                else if (current.statement == CrplCore.STATEMENT.IF)
                {
                    num2++;
                    stack.Push(current.lineNumber);
                }
                else if (current.statement == CrplCore.STATEMENT.ENDIF)
                {
                    num2--;
                    if (num2 < 0)
                    {
                        message = "Line " + current.lineNumber + ": An 'endif' is missing a matching 'if'";
                        bool result = false;
                        return result;
                    }
                    stack.Pop();
                }
                else if (current.statement == CrplCore.STATEMENT.ELSE)
                {
                    if (num2 <= 0)
                    {
                        message = "Line " + current.lineNumber + ": An 'else' must be nested within an 'if'";
                        bool result = false;
                        return result;
                    }
                }
                else if (current.statement == CrplCore.STATEMENT.ONCE)
                {
                    num6++;
                    stack4.Push(current.lineNumber);
                }
                else if (current.statement == CrplCore.STATEMENT.ENDONCE)
                {
                    num6--;
                    if (num6 < 0)
                    {
                        message = "Line " + current.lineNumber + ": An 'endonce' is missing a matching 'once'";
                        bool result = false;
                        return result;
                    }
                    stack4.Pop();
                }
                else if (current.statement == CrplCore.STATEMENT.WHILE)
                {
                    num3++;
                    num4++;
                    stack2.Push(current.lineNumber);
                }
                else if (current.statement == CrplCore.STATEMENT.ENDWHILE)
                {
                    num4--;
                    if (num4 < 0)
                    {
                        message = "Line " + current.lineNumber + ": An 'endwhile' is missing a matching 'while'";
                        bool result = false;
                        return result;
                    }
                    stack2.Pop();
                }
                else if (current.statement == CrplCore.STATEMENT.REPEAT)
                {
                    num8++;
                    if (num4 <= 0)
                    {
                        message = "Line " + current.lineNumber + ": A 'repeat' must not be found outside of a 'while/endwhile block'";
                        bool result = false;
                        return result;
                    }
                }
                else if (current.statement == CrplCore.STATEMENT.BREAK)
                {
                    if (num4 <= 0 && num5 <= 0)
                    {
                        message = "Line " + current.lineNumber + ": A 'break' must be found inside a 'while/endwhile block' or if 'do/loop' block";
                        bool result = false;
                        return result;
                    }
                }
                else if (current.statement == CrplCore.STATEMENT.DO)
                {
                    num5++;
                    stack3.Push(current.lineNumber);
                }
                else if (current.statement == CrplCore.STATEMENT.LOOP)
                {
                    num5--;
                    if (num5 < 0)
                    {
                        message = "Line " + current.lineNumber + ": A 'loop' is missing a matching 'do'";
                        bool result = false;
                        return result;
                    }
                    stack3.Pop();
                }
                else if (current.statement == CrplCore.STATEMENT.FUNC)
                {
                    if (num2 > 0 || num5 > 0 || num6 > 0)
                    {
                        message = "Line " + current.lineNumber + ": Functions must not be nested within conditionals or loops";
                        bool result = false;
                        return result;
                    }
                    if (current.data.GetString() == "DESTROYED")
                    {
                        num++;
                        if (num > 1)
                        {
                            message = "Line " + current.lineNumber + ": Only one :Destroy label is permitted";
                            bool result = false;
                            return result;
                        }
                    }
                }
            }
            if (num7 > 0)
            {
                int num9 = -1;
                try
                {
                    num9 = stack5.Peek();
                }
                catch (Exception)
                {
                }
                message = "Line " + num9 + ": An '(' is missing a matching ')'";
                return false;
            }
            if (num2 > 0)
            {
                int num10 = -1;
                try
                {
                    num10 = stack.Peek();
                }
                catch (Exception)
                {
                }
                message = "Line " + num10 + ": An 'if' is missing a matching 'endif'";
                return false;
            }
            if (num4 > 0)
            {
                int num11 = -1;
                try
                {
                    num11 = stack2.Peek();
                }
                catch (Exception)
                {
                }
                message = "Line " + num11 + ": A 'while' is missing a matching 'endwhile'";
                return false;
            }
            if (num5 > 0)
            {
                int num12 = -1;
                try
                {
                    num12 = stack3.Peek();
                }
                catch (Exception)
                {
                }
                message = "Line " + num12 + ": A 'do' is missing a matching 'loop'";
                return false;
            }
            if (num6 > 0)
            {
                int num13 = -1;
                try
                {
                    num13 = stack4.Peek();
                }
                catch (Exception)
                {
                }
                message = "Line " + num13 + ": A 'once' is missing a matching 'endonce'";
                return false;
            }
            if (num3 != num8)
            {
                message = "Line unknown: Every 'while/endwhile' block must contain a 'repeat' statement";
                return false;
            }
            message = string.Empty;
            return true;
        }

        private CrplCore.Command AppendCommand(CrplCore.STATEMENT statement, int lineNumber)
        {
            CrplCore.Command command = new CrplCore.Command(statement, lineNumber);
            this.commands.Add(command);
            return command;
        }

        private CrplCore.Command AppendCommand(CrplCore.STATEMENT statement, int arg, int lineNumber)
        {
            CrplCore.Command command = new CrplCore.Command(statement, arg, lineNumber);
            this.commands.Add(command);
            return command;
        }

        private CrplCore.Command AppendCommand(CrplCore.STATEMENT statement, float arg, int lineNumber)
        {
            CrplCore.Command command = new CrplCore.Command(statement, arg, lineNumber);
            this.commands.Add(command);
            return command;
        }

        private CrplCore.Command AppendCommand(CrplCore.STATEMENT statement, string arg, int lineNumber)
        {
            CrplCore.Command command = new CrplCore.Command(statement, arg, lineNumber);
            this.commands.Add(command);
            return command;
        }

        private void DeleteCommand(int index)
        {
            this.commands.RemoveAt(index);
        }

        private int FindJumpPoint(int currentCommandIndex)
        {
            int num = 0;
            int i;
            for (i = currentCommandIndex + 1; i < this.commands.Count; i++)
            {
                CrplCore.Command command = this.commands[i];
                if (command.statement == CrplCore.STATEMENT.IF)
                {
                    num++;
                }
                if (num > 0)
                {
                    if (command.statement == CrplCore.STATEMENT.ENDIF)
                    {
                        num--;
                    }
                }
                else if (command.statement == CrplCore.STATEMENT.ELSE || command.statement == CrplCore.STATEMENT.ENDIF)
                {
                    break;
                }
            }
            return i;
        }

        private int FindCloseTran(int currentCommandIndex)
        {
            int num = 0;
            for (int i = currentCommandIndex + 1; i < this.commands.Count; i++)
            {
                CrplCore.Command command = this.commands[i];
                if (command.statement == CrplCore.STATEMENT.OPENTRAN)
                {
                    num++;
                }
                if (num > 0)
                {
                    if (command.statement == CrplCore.STATEMENT.CLOSETRAN)
                    {
                        num--;
                    }
                }
                else if (command.statement == CrplCore.STATEMENT.CLOSETRAN)
                {
                    return i;
                }
            }
            return -1;
        }

        private int FindCloseBracket(int currentCommandIndex)
        {
            int num = 0;
            for (int i = currentCommandIndex + 1; i < this.commands.Count; i++)
            {
                CrplCore.Command command = this.commands[i];
                if (command.statement == CrplCore.STATEMENT.OPENBRACKET)
                {
                    num++;
                }
                if (num > 0)
                {
                    if (command.statement == CrplCore.STATEMENT.CLOSEBRACKET)
                    {
                        num--;
                    }
                }
                else if (command.statement == CrplCore.STATEMENT.CLOSEBRACKET)
                {
                    return i;
                }
            }
            return -1;
        }

        private int FindLoop(int currentCommandIndex)
        {
            int num = 0;
            int i;
            for (i = currentCommandIndex + 1; i < this.commands.Count; i++)
            {
                CrplCore.Command command = this.commands[i];
                if (command.statement == CrplCore.STATEMENT.DO)
                {
                    num++;
                }
                if (num > 0)
                {
                    if (command.statement == CrplCore.STATEMENT.LOOP)
                    {
                        num--;
                    }
                }
                else if (command.statement == CrplCore.STATEMENT.LOOP)
                {
                    break;
                }
            }
            return i;
        }

        private int FindEndOnce(int currentCommandIndex)
        {
            int num = 0;
            int i;
            for (i = currentCommandIndex + 1; i < this.commands.Count; i++)
            {
                CrplCore.Command command = this.commands[i];
                if (command.statement == CrplCore.STATEMENT.ONCE)
                {
                    num++;
                }
                if (num > 0)
                {
                    if (command.statement == CrplCore.STATEMENT.ENDONCE)
                    {
                        num--;
                    }
                }
                else if (command.statement == CrplCore.STATEMENT.ENDONCE)
                {
                    break;
                }
            }
            return i;
        }

        private int FindEndWhile(int currentCommandIndex)
        {
            int num = 0;
            int i;
            for (i = currentCommandIndex + 1; i < this.commands.Count; i++)
            {
                CrplCore.Command command = this.commands[i];
                if (command.statement == CrplCore.STATEMENT.WHILE)
                {
                    num++;
                }
                if (num > 0)
                {
                    if (command.statement == CrplCore.STATEMENT.ENDWHILE)
                    {
                        num--;
                    }
                }
                else if (command.statement == CrplCore.STATEMENT.ENDWHILE)
                {
                    break;
                }
            }
            return i;
        }

        private int FindDoOrEndWhile(int currentCommandIndex)
        {
            int num = 0;
            int num2 = 0;
            int i;
            for (i = currentCommandIndex + 1; i < this.commands.Count; i++)
            {
                CrplCore.Command command = this.commands[i];
                if (command.statement == CrplCore.STATEMENT.WHILE)
                {
                    num++;
                }
                else if (command.statement == CrplCore.STATEMENT.DO)
                {
                    num2++;
                }
                if (num > 0)
                {
                    if (command.statement == CrplCore.STATEMENT.ENDWHILE)
                    {
                        num--;
                    }
                }
                else if (num2 > 0)
                {
                    if (command.statement == CrplCore.STATEMENT.LOOP)
                    {
                        num2--;
                    }
                }
                else if (command.statement == CrplCore.STATEMENT.ENDWHILE || command.statement == CrplCore.STATEMENT.LOOP)
                {
                    break;
                }
            }
            return i;
        }
    }//*/

}
