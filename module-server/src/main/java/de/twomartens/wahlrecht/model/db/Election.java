package de.twomartens.wahlrecht.model.db;

import de.twomartens.wahlrecht.model.dto.v1.VotingThreshold;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.data.mongodb.core.index.Indexed;
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
public class Election {

  @Id
  ObjectId id;
  @CreatedDate
  Date created;
  @LastModifiedDate
  Date lastModified;

  @Indexed(unique = true)
  String name;
  LocalDate day;
  VotingThreshold votingThreshold;
  int totalNumberOfSeats;
  Collection<Constituency> constituencies;
}
