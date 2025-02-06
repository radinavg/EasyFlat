package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.util.Date;

public class RepeatChoreRequest {
    private Long id;
    private Date date;

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setId(long l) {
        this.id = l;
    }

    public void setDate(Date now) {
        this.date = now;
    }
}
