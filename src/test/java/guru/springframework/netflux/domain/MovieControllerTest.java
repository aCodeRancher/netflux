package guru.springframework.netflux.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
class MovieControllerTest {


     @Autowired
     private WebTestClient webTestClient;


     @Test
     void testGetMovieEvent() throws InterruptedException {

         CountDownLatch countDownLatch = new CountDownLatch(2);
          List<Movie> moviesList = webTestClient.get().uri("/movies").exchange()
                 .expectBodyList(Movie.class).returnResult().getResponseBody();
          Movie firstMovie = moviesList.get(0);
          final  String id = firstMovie.getId();


            webTestClient.get().uri("/movies/"+firstMovie.getId()+"/events")
                   .accept(MediaType.TEXT_EVENT_STREAM)
                   .exchange().returnResult(MovieEvent.class).getResponseBody().take(2)
                  .subscribe( movies -> { movies.getMovieId().equals(id); countDownLatch.countDown();} );



          countDownLatch.await();
          assertThat(countDownLatch.getCount()).isEqualTo(0);
     }
}