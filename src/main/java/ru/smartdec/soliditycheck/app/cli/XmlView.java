package ru.smartdec.soliditycheck.app.cli;

import ru.smartdec.soliditycheck.DocumentTreeBasic;
import ru.smartdec.soliditycheck.ParseTreeBasic;
import ru.smartdec.soliditycheck.SolidityParser;
import ru.smartdec.soliditycheck.SourceFile;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public final class XmlView {

    /**
     * @param args args
     * @throws Exception Exception
     */
    public static void main(final String... args) throws Exception {
//        XmlView.main(new ArgumentsDefault(args));
        XmlView.main(new ArgumentsDefault("-s","tests_not_used_now\\SOLIDITY_INTEGER_OVERFLOW_ADD.sol","-t","src\\main\\resources\\test.xml"));
    }

    /**
     * @param arguments args
     * @throws Exception exception
     */
    public static void main(final Arguments arguments) throws Exception {
        new XmlView(
                arguments
                        .value("-s", "-source")
                        .map(Paths::get)
                        .orElseThrow(IllegalArgumentException::new),
                arguments
                        .value("-t", "-target")
                        .map(Paths::get)
                        .orElseThrow(IllegalArgumentException::new)
        )
                .run();
    }

    /**
     *
     */
    private final Path source;
    /**
     *
     */
    private final Path target;

    /**
     * @param src source
     * @param tr  target
     */
    public XmlView(final Path src, final Path tr) {
        this.source = src;
        this.target = tr;
    }

    /**
     * @throws Exception exception
     */
    public void run() throws Exception {
        final Transformer transformer = TransformerFactory
                .newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(
                new DOMSource(
                        new DocumentTreeBasic(
                                new ParseTreeBasic(new SourceFile(this.source)),
                                DocumentBuilderFactory
                                        .newInstance()
                                        .newDocumentBuilder(),
                                SolidityParser.ruleNames
                        )
                                .info()
                                .node()
                ),
                new StreamResult(this.target.toFile())
        );
    }
}
