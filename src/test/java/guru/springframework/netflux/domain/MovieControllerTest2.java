package guru.springframework.netflux.domain;

import guru.springframework.netflux.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class MovieControllerTest2 {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    public void setUp(){
        movieRepository.deleteAll()
                .thenMany(
                        Flux.just("Aladdin", "Beauty and the Beast")
                                .map(Movie::new)
                                .flatMap(movieRepository::save)
                ).blockLast();

    }

    @Test
    public void getMovies(){

        Flux<Movie> movies = webTestClient.get().uri("/movies").exchange().returnResult(Movie.class).getResponseBody();
        StepVerifier.create(movies)
                    .expectNextCount(2)
                    .verifyComplete();
   }

    @Test
    public void getMoviesTitles() {
        webTestClient.get().uri("/movies").exchange()
                .expectBodyList(Movie.class)
                .consumeWith(response -> {
                                      List<Movie> movies = response.getResponseBody();
                                      movies.forEach(mov -> System.out.println(mov.getTitle()));
                                      });
    }
}
