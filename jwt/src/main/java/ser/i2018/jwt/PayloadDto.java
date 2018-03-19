package ser.i2018.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PayloadDto {
    private String attr;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public static PayloadDto fromString(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.reader().forType(PayloadDto.class).readValue(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
