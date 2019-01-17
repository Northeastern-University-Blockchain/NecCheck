package ru.smartdec.soliditycheck.app.cli;

import ru.smartdec.soliditycheck.RulesCached;
import ru.smartdec.soliditycheck.RulesXml;
import ru.smartdec.soliditycheck.app.DirectoryAnalysisDefault;
import ru.smartdec.soliditycheck.app.ReportDefault;
import ru.smartdec.soliditycheck.app.TreeFactoryDefault;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public final class Tool {

    /**
     * @param args args
     * @throws Exception exception
     */
    public static void main(final String... args) throws Exception {
        Tool.main(new ArgumentsDefault("-p","F:\\result\\testCase50","-r","src\\main\\resources\\rules.xml"));
    }

    /**
     * @param arguments args
     * @throws Exception exception
     */
    public static void main(final Arguments arguments) throws Exception {
        Path src = arguments
                .value("-p", "-path")
                .map(Paths::get)
                .filter(Files::exists)
                .orElseThrow(IllegalArgumentException::new);

        RulesXml.Source defaultRules = () -> {
            URI uri = RulesXml
                    .class
                    .getResource("/rules.xml")
                    .toURI();
            System.out.print(uri);

            // initialize a new ZipFilesystem
            HashMap<String, String> env = new HashMap<>();
            env.put("create", "true");
            FileSystems.newFileSystem(uri, env);

            return Paths.get(uri);
        };

        RulesXml.Source rules = arguments
                .value("-r", "-rules")
                .map(Paths::get)
                .filter(Files::isRegularFile)
                .<RulesXml.Source>map(path -> () -> path)
                .orElseGet(() -> defaultRules);

        new Tool(src, rules).run();
    }

    /**
     *
     */
    private final Path source;
    /**
     *
     */
    private final RulesXml.Source rules;

    /**
     * @param src source
     * @param rs  rules
     */
    public Tool(final Path src, final RulesXml.Source rs) {
        this.source = src;
        this.rules = rs;
    }

    /**
     * @throws Exception exception
     */
    public void run() throws Exception {
        File file = new File("F:/newfile.txt");//changed by zyx

        new ReportDefault(
                new DirectoryAnalysisDefault(
                        this.source,
                        new TreeFactoryDefault(
                                DocumentBuilderFactory
                                        .newInstance()
                                        .newDocumentBuilder()
                        ),
                        new RulesCached(
                                new RulesXml(
                                        this.rules,
                                        XPathFactory.newInstance().newXPath(),
                                        Throwable::printStackTrace
                                )
                        )
                ),
                info -> {
                    System.out.println(info.file());
                    String filename = "\n"+info.file()+"\n";//changed by zyx
                    try(FileOutputStream fop = new FileOutputStream(file,true)){
                        byte[] contentInBytes = filename.getBytes();
                        fop.write(contentInBytes);
                        fop.flush();
                        fop.close();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }//changed by zyx
                    Map<String, Integer> result = new HashMap<>();
                    info.treeReport().streamUnchecked().forEach(
                            tree -> tree.contexts().forEach(
                                    context -> {
                                        //changed by zyx
                                        /*System.out.printf(
                                                "ruleId: %s%npatternId: %s%n"
                                                      + "line: %d%ncolumn: %d%n"
                                                      + "content: %s%n%n",
                                                tree.rule().id(),
                                                tree.pattern().id(),
                                                context.getStart().getLine(),
                                                context
                                                       .getStart()
                                                       .getCharPositionInLine(),
                                                context.getText()
                                        );*/
                                        result.compute(
                                                tree.rule().id(),
                                                (k, v) -> Optional
                                                        .ofNullable(v)
                                                        .map(i -> i + 1)
                                                        .orElse(1)
                                        );
                                    }
                            )
                    );
                    result.forEach((k, v) -> RUA(k,v,file));//changed by zyx
                    //System.out.println(k + " :" + v)); //changed by zyx

                }
        )
                .print();
    }
    //add by zyx
    public void RUA(String k,Integer v,File f)
    {
        try(FileOutputStream fop = new FileOutputStream(f,true)){
            String content =  k + " :" + v+"\n";
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}
