package guru.springframework.netflux;

import guru.springframework.netflux.domain.Movie;
import guru.springframework.netflux.repositories.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
class NetfluxApplicationTests {

    @Autowired
    MovieRepository movieRepository;

    @Test
    void contextLoads() {
        Flux<Movie> movieFlux = movieRepository.findAll();
        movieFlux.subscribe( movie -> {System.out.println(movie.getTitle());});

    }

}
