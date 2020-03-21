package be.stijnhooft.portal.todo.model.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionMappingToTask {

    /**
     * How should the name of the task be assembled? Should be a Spring EL expression.
     * Example:
     *          'Please do ' + source
     **/
    @NonNull
    private String mappingOfName;

    /**
     * How should the description of the task be assembled? Should be a Spring EL expression.
     * Example:
     *          'You should do ' + source + ', specifically '+ data['someProperty']
     **/
    private String mappingOfDescription;

    /**
     * When should the task be finished?
     * Should be a Spring EL expression resulting in a string that looks like a date.
     **/
    private String mappingOfDueDate;

    /**
     * What is the context of the task? Should be a Spring EL expression.
     * Example:
     *          'Personal'
     **/
    @NonNull
    private String mappingOfContext;

    /**
     * What is the importance of the task? Should be a Spring EL expression.
     * Example:
     *          'VERY_IMPORTANT'
     **/
    private String mappingOfImportance;

    /**
     * Alternative to getter, to make code more concise. Meant to be used as: mapping.ofName();
     **/
    public String ofName() {
        return getMappingOfName();
    }

    /**
     * Alternative to getter, to make code more concise. Meant to be used as: mapping.ofDescription();
     **/
    public String ofDescription() {
        return getMappingOfDescription();
    }

    /**
     * Alternative to getter, to make code more concise. Meant to be used as: mapping.ofDueDate();
     **/
    public String ofDueDate() {
        return getMappingOfDueDate();
    }

    /**
     * Alternative to getter, to make code more concise. Meant to be used as: mapping.ofContext();
     **/
    public String ofContext() {
        return getMappingOfContext();
    }

    /**
     * Alternative to getter, to make code more concise. Meant to be used as: mapping.ofImportance();
     **/
    public String ofImportance() {
        return getMappingOfImportance();
    }

}
