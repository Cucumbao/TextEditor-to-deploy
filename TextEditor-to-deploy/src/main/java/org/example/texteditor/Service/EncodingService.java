package org.example.texteditor.Service;

import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

@Service
public class EncodingService {

    public String detectEncoding(byte[] bytes) {
        if (isValidUTF8(bytes)) {
            return StandardCharsets.UTF_8.name();
        }
        return "windows-1251";
    }

    private boolean isValidUTF8(byte[] bytes) {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

        try {
            decoder.decode(ByteBuffer.wrap(bytes));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }
}
