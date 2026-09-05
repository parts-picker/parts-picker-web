package de.partspicker.web.user.api.resources

import de.partspicker.web.user.business.objects.User
import de.partspicker.web.user.business.objects.enums.UserType
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = UserResource.COLLECTION_RELATION_NAME)
class UserResource(
    val id: Long,
    val username: String,
    val displayName: String?,
    val type: UserType,
    links: Iterable<Link> = emptyList()
) : RepresentationModel<UserResource>(links) {

    companion object {
        const val COLLECTION_RELATION_NAME = "users"

        fun from(user: User) = UserResource(
            id = user.id,
            username = user.username,
            displayName = user.displayName,
            type = user.type
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as UserResource

        if (id != other.id) return false
        if (username != other.username) return false
        if (displayName != other.displayName) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "UserResource(id=$id, username='$username', displayName=$displayName, type=$type)"
    }
}
