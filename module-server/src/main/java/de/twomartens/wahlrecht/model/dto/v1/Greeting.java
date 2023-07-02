package de.twomartens.wahlrecht.model.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Data container for a greeting message")
public class Greeting {

  @NonNull
  @Schema(description = "Data container for a greeting message", example = "Hello Helmut!",
      defaultValue = "Hello World!")
  private final String message;

}
