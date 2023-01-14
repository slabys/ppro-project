package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table()
@NoArgsConstructor
public class Comment extends BaseModel {
    @OneToOne(targetEntity = User.class) @Getter @Setter
    private User createdBy;

    @Column() @ColumnDefault("false") @Getter @Setter
    private boolean deleted;

    @Column() @Getter @Setter
    private String content;


}