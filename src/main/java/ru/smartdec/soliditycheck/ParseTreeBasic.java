package ru.smartdec.soliditycheck;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.RuleNode;
import ru.smartdec.soliditycheck.SolidityParser;
import ru.smartdec.soliditycheck.SolidityLexer;
/**
 *
 */
public final class ParseTreeBasic implements ParseTree {

    /**
     *
     */
    private final Source source;

    /**
     * @param src source
     */
    public ParseTreeBasic(final Source src) {
        this.source = src;
    }

    @Override
    public RuleNode root() throws Exception {
        return new SolidityParser(
                new CommonTokenStream(
                        new SolidityLexer(
                                this.source.chars()
                        )
                )
        )
                .sourceUnit();
    }
}
