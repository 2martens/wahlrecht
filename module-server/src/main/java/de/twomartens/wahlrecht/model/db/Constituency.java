package de.twomartens.wahlrecht.model.db;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@CompoundIndex(def = "{'number': 1, 'electionName': 1}")
public class Constituency {

  @EqualsAndHashCode.Exclude
  @Id
  ObjectId id;
  @EqualsAndHashCode.Exclude
  @CreatedDate
  Instant created;
  @EqualsAndHashCode.Exclude
  @LastModifiedDate
  Instant lastModified;

  int number;
  String electionName;
  String name;
  int numberOfSeats;
}
