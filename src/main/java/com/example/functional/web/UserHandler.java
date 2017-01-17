package com.example.functional.web;

import com.example.functional.domain.User;
import com.example.functional.domain.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class UserHandler {

	private final UserRepository repository;

	public UserHandler(UserRepository repository) {
		this.repository = repository;
	}

	public Mono<ServerResponse> listPeople(ServerRequest request) {
		Flux<User> people = this.repository.findAll();
		return ServerResponse.ok().body(people, User.class);
	}

	public Mono<ServerResponse> getUser(ServerRequest request) {
		String userId = request.pathVariable("id");
		Mono<ServerResponse> notFound = ServerResponse.notFound().build();
		return this.repository.findOne(userId)
				.then(user -> ServerResponse.ok().body(Mono.just(user), User.class))
				.otherwiseIfEmpty(notFound);
	}

	public Mono<ServerResponse> createUser(ServerRequest request) {
		Mono<User> user = request.bodyToMono(User.class);
		return ServerResponse.ok().build(this.repository.save(user).then());
	}

}
