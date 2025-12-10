package org.example;

import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

@Entity
public class Organization {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@OneToMany(mappedBy = "organization", cascade = CascadeType.PERSIST)
    //private List<Member> members = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Member> member = new HashSet<>();

    public void addMember(Member member){
        this.member.add(member);
        //member.setOrganization(this);
    }

    public void removeMember(Member member){
        this.member.remove(member);
        //member.setOrganization(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Organization that = (Organization) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ")";
    }
}
