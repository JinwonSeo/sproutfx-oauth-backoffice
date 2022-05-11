package kr.sproutfx.oauth.backoffice.api.project.service;

import kr.sproutfx.oauth.backoffice.api.project.entity.Project;
import kr.sproutfx.oauth.backoffice.api.project.enumeration.ProjectStatus;
import kr.sproutfx.oauth.backoffice.api.project.exception.ProjectNotFoundException;
import kr.sproutfx.oauth.backoffice.api.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return this.projectRepository.findAll();
    }

    public List<Project> findAllWithClients() {
        return this.projectRepository.findAllWithClients();
    }

    public Project findById(UUID id) {
        return this.projectRepository.findByIdWithClients(id).orElseThrow(ProjectNotFoundException::new);
    }

    @Transactional
    public UUID create(String name, String description) {
        Project persistenceProject = this.projectRepository.save(Project.builder()
            .name(name)
            .description(description)
            .status(ProjectStatus.PENDING_APPROVAL)
            .build());

        return persistenceProject.getId();
    }

    @Transactional
    public void update(UUID id, String name, String description) {
        Project persistenceProject = this.projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);

        persistenceProject.setName(name);
        persistenceProject.setDescription(description);
    }

    @Transactional
    public void updateStatus(UUID id, ProjectStatus projectStatus) {
        Project persistenceProject = this.projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);

        persistenceProject.setStatus(projectStatus);
    }

    @Transactional
    public void delete(UUID id) {
        Project persistenceProject = this.projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);

        this.projectRepository.delete(persistenceProject);
    }

}
