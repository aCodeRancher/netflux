package guru.springframework.netflux.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest//(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)

public class MovieControllerTest1 {

    public static final String BASE_URL = "http://localhost:8080";

    WebClient webClient;

    AtomicReference<String> m = new AtomicReference<>(new String());
    String anyMovieID = new String();

    @BeforeEach
    void setUp(){
        webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true))).build();
    }

    @Test
    void testGetMovieEvent() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Movie> movies = webClient.get().uri("/movies").retrieve()
                .bodyToFlux(Movie.class);
        movies.subscribe(movie -> {assertThat(movie).isNotNull();
            m.set(movie.getId());
            anyMovieID= m.get();
        });
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);

        Flux<MovieEvent> moviesEvent = webClient.get().uri("/movies/"+anyMovieID+"/events")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(MovieEvent.class).take(1);
        Mono<List<MovieEvent>> monoMovie = moviesEvent.collectList();
        monoMovie.publishOn(Schedulers.parallel()).subscribe(list -> { assertThat(list.size()==1);
            countDownLatch.countDown();});
        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(countDownLatch.getCount()).isEqualTo(1);
    }
}
