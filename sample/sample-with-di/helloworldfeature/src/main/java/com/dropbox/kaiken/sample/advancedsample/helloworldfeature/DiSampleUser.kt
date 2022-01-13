package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import com.dropbox.kaiken.skeleton.core.SkeletonAccessToken
import com.dropbox.kaiken.skeleton.core.SkeletonUser
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.usermanagement.SkeletonMapper
import com.dropbox.kaiken.skeleton.usermanagement.UserMapper
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiSampleUser(val userId: String, val accessToken: SkeletonAccessToken, val name: String)

class RealSkeletonUserMapper : SkeletonMapper<DiSampleUser> {
    override fun toSkeletonUser(from: DiSampleUser): SkeletonUser {
        return SkeletonUser(from.userId, from.accessToken)
    }
}

class RealUserMapper @Inject constructor(private val accountStore: AccountStore, private val mapper: SkeletonMapper<DiSampleUser>) : UserMapper {
    override fun users(): Flow<Set<SkeletonUser>> =
        accountStore.getAllUsers().map { sampleUsers ->
            sampleUsers.map { diSampleUser ->
                mapper.toSkeletonUser(diSampleUser)
            }.toSet()
        }
}

@Module
@ContributesTo(AppScope::class)
class BindingModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSkeletonMapper(): SkeletonMapper<DiSampleUser> {
        return RealSkeletonUserMapper()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideUserMapper(accountStore: AccountStore, mapper: SkeletonMapper<DiSampleUser>): UserMapper {
        return RealUserMapper(accountStore, mapper)
    }
}
