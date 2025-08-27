package co.com.pragma.bootcamp.usecase.registrarsolicitud.helper;

import co.com.pragma.bootcamp.model.state.State;

public enum ApplicationState {
    PENDING_REVIEW(1, "PENDING_REVIEW", "Application is pending review"),
    APPROVED(2, "APPROVED", "Application has been approved"),
    REJECTED(3, "REJECTED", "Application has been rejected");

    private final Integer id;
    private final String name;
    private final String description;

    ApplicationState(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public State toDomain() {
        return State.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}