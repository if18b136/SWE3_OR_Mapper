package Entities;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Annotations.Table;

@Table(name = "t_Test")
public class Testing {
    @Column(primary = true)
    private int id;

    @Column(ignore = true)
    private String ignored;

    @Column(length = 35)
    private String text35;

    public Testing(int id, String ignored, String text35) {
        this.id = id;
        this.ignored = ignored;
        this.text35 = text35;
    }
}
