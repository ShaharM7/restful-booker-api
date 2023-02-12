package payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AuthResponsePayload {

    @JsonProperty
    private String token;
}
