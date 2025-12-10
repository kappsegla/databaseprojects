package org.example;

import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NamedEntityGraph(
        name = "Organization.member",
        attributeNodes = @NamedAttributeNode("member")
)
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Member> member = new HashSet<>();

    public void addMember(Member member) {
        this.member.add(member);
        member.addOrganization(this);
    }

    public void removeMember(Member member) {
        this.member.remove(member);
        member.removeOrganization(this);
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
        return "Organization{" +
                "id=" + id +
                ", member=" + member +
                '}';
    }
}
