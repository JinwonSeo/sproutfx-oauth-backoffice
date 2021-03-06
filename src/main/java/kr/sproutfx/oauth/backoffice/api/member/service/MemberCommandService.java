package kr.sproutfx.oauth.backoffice.api.member.service;

import kr.sproutfx.oauth.backoffice.api.member.entity.Member;
import kr.sproutfx.oauth.backoffice.api.member.enumeration.MemberStatus;
import kr.sproutfx.oauth.backoffice.api.member.exception.MemberNotFoundException;
import kr.sproutfx.oauth.backoffice.api.member.repository.MemberRepository;
import kr.sproutfx.oauth.backoffice.api.member.repository.specification.MemberSpecification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class MemberCommandService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberCommandService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UUID create(String email, String name, String password, String description) {
        Member persistenceMember = this.memberRepository.save(Member.builder()
            .email(email)
            .name(name)
            .password(this.passwordEncoder.encode(password))
            .passwordExpired(LocalDateTime.now().plusDays(90)) // To-do: password 90일 하드코딩 password 정책관리 기능 추가 후 변수화
            .status(MemberStatus.PENDING_APPROVAL)
            .description(description)
            .build());

        return persistenceMember.getId();
    }

    public void update(UUID id, String email, String name, String description) {
        Member persistenceMember = this.memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);

        persistenceMember.update(email, name, description);
    }

    public UUID updatePassword(String email, String currentPassword, String newPassword) {
        Member persistenceMember = this.memberRepository.findOne(MemberSpecification.equalEmail(email)).orElseThrow(MemberNotFoundException::new);

        persistenceMember.updatePassword(passwordEncoder, currentPassword, newPassword);

        return persistenceMember.getId();
    }

    public void updateStatus(UUID id, MemberStatus memberStatus) {
        Member persistenceMember = this.memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);

        persistenceMember.updateStatus(memberStatus);
    }

    public void deleteById(UUID id) {
        Member persistenceMember = this.memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);

        this.memberRepository.delete(persistenceMember);
    }
}
