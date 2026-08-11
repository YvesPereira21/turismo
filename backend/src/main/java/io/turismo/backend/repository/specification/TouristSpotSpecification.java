package io.turismo.backend.repository.specification;  
  
import io.turismo.backend.model.TouristSpot;  
import jakarta.persistence.criteria.CriteriaBuilder;  
import jakarta.persistence.criteria.CriteriaQuery;  
import jakarta.persistence.criteria.Predicate;  
import jakarta.persistence.criteria.Root;  
import org.jspecify.annotations.Nullable;  
import org.locationtech.jts.geom.Point;  
import org.springframework.data.jpa.domain.Specification;  
  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Set;  
  
public class TouristSpotSpecification implements Specification<TouristSpot> {  
  
    private String name;  
    private String cityName;  
    private String stateName;  
    private Set<String> tags;  
    private Point userLocation;  
    private Double radius;  
  
    public TouristSpotSpecification(String name, String cityName, String stateName, Set<String> tags, Point userLocation, Double radius) {  
        this.name = name;  
        this.cityName = cityName;  
        this.stateName = stateName;  
        this.tags = tags;  
        this.userLocation = userLocation;  
        this.radius = radius;  
    }  

    @Override  
    public @Nullable Predicate toPredicate(  
            Root<TouristSpot> root,  
            CriteriaQuery<?> query,  
            CriteriaBuilder criteriaBuilder  
    ) {  
        List<Predicate> predicates = new ArrayList<>();  
  
        if (query != null && Long.class != query.getResultType()) {  
            query.distinct(true);  
        }  
        // Filtro por nome do ponto  
        if (this.name != null && !this.name.isBlank()) {  
            predicates.add(  
                    criteriaBuilder.like(  
                            criteriaBuilder.lower(root.get("name")),  
                            "%" + this.name.trim().toLowerCase() + "%"  
                    )  
            );        
        }  
        // Filtro por nome da cidade  
        if (this.cityName != null && !this.cityName.isBlank()) {  
            predicates.add(  
                    criteriaBuilder.like(  
                            criteriaBuilder.lower(root.join("city").get("name")),  
                            "%" + this.cityName.trim().toLowerCase() + "%"  
                    )  
            );        
        }  
        // Filtro por nome do estado  
        if (this.stateName != null && !this.stateName.isBlank()) {  
            predicates.add(  
                    criteriaBuilder.like(  
                            criteriaBuilder.lower(root.join("city").join("state").get("name")),  
                            "%" + this.stateName.trim().toLowerCase() + "%"  
                    )  
            );        
        }  
        // Filtro por tag (lista)  
        if (this.tags != null && !this.tags.isEmpty()) {  
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(  
                    criteriaBuilder.lower(root.join("tags").get("name"))  
            );            
            for (String tag : this.tags) {  
                inClause.value(tag.trim().toLowerCase());  
            }            
            predicates.add(inClause);  
        }  
        // Filtro por distância  
        if (this.userLocation != null && this.radius != null) {  
            predicates.add(  
                    criteriaBuilder.lessThanOrEqualTo(  
                            criteriaBuilder.function(  
                                    "distance",  
                                    Double.class,  
                                    root.get("location"),  
                                    criteriaBuilder.literal(this.userLocation)  
                            ),                            
                            this.radius  
                    )  
            );        
        }  
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));  
    }
}
