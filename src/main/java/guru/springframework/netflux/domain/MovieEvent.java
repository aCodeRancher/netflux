package guru.springframework.netflux.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by jt on 3/6/21.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieEvent {
    private String movieId;
    private Date movieDate;
 }
