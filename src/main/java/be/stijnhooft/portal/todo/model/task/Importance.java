package be.stijnhooft.portal.todo.model.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Importance {

    I_DO_NOT_REALLY_CARE(0),
    NOT_SO_IMPORTANT(1),
    VERY_IMPORTANT(2);

    private final int representationOfImportanceAsANumber;

}
