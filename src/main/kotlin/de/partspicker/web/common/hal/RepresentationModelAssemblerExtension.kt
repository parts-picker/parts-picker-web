package de.partspicker.web.common.hal

import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.RepresentationModelAssembler

fun <T : Any, D : RepresentationModel<*>> RepresentationModelAssembler<T, D>.toCollectionModel(
    objects: Iterable<T>,
    vararg links: Link
): CollectionModel<D> = CollectionModel.of(objects.map(this::toModel), *links)
