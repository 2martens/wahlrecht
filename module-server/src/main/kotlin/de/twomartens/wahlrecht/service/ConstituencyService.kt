package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.db.Constituency
import de.twomartens.wahlrecht.repository.ConstituencyRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ConstituencyService(private val repository: ConstituencyRepository) {

  fun storeConstituencies(constituencies: Collection<Constituency>): Flux<Constituency> {
    return Flux.merge(constituencies.map { repository.findAndModify(it) })
  }
}
