package com.virgile.listuser

import com.virgile.listuser.data.local.repository.ContactLocalRepository
import com.virgile.listuser.data.local.entity.ContactLocal
import com.virgile.listuser.data.remote.model.Coordinates
import com.virgile.listuser.data.remote.model.Dob
import com.virgile.listuser.data.remote.model.Id
import com.virgile.listuser.data.remote.model.Info
import com.virgile.listuser.data.remote.model.Location
import com.virgile.listuser.data.remote.model.Login
import com.virgile.listuser.data.remote.model.Name
import com.virgile.listuser.data.remote.model.Picture
import com.virgile.listuser.data.remote.model.Registered
import com.virgile.listuser.data.remote.model.Response
import com.virgile.listuser.data.remote.model.Result
import com.virgile.listuser.data.remote.model.Street
import com.virgile.listuser.data.remote.model.Timezone
import com.virgile.listuser.usecases.toContact
import com.virgile.listuser.data.remote.repositories.ContactRemoteRepository
import com.virgile.listuser.usecases.CleanContactListUseCaseImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CleanContactListUseCaseImplTest {

    @Mock
    private lateinit var contactRemoteRepository: ContactRemoteRepository

    @Mock
    private lateinit var contactLocalRepository: ContactLocalRepository

    private lateinit var cleanContactListUseCaseImpl: CleanContactListUseCaseImpl

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        cleanContactListUseCaseImpl = CleanContactListUseCaseImpl(contactRemoteRepository, contactLocalRepository)
    }

    @Test
    fun `invoke should clear the database and fetch remote data`() = runBlocking {
        // Arrange
        val remoteData = Response(
            results = listOf(
                Result(
                    gender = "male",
                    name = Name("Mr", "John", "Doe"),
                    location = Location(
                        street = Street(123, "Main Street"),
                        city = "City",
                        state = "State",
                        country = "Country",
                        postcode = "12345",
                        coordinates = Coordinates("123.456", "789.012"),
                        timezone = Timezone("UTC+1", "Central European Time")
                    ),
                    email = "john.doe@example.com",
                    login = Login(
                        uuid = "uuid",
                        username = "johndoe",
                        password = "password",
                        salt = "salt",
                        md5 = "md5",
                        sha1 = "sha1",
                        sha256 = "sha256"
                    ),
                    dob = Dob("1990-01-01", 31),
                    registered = Registered("2020-01-01", 2),
                    phone = "1234567890",
                    cell = "0987654321",
                    id = Id(name = "ID", value = null),
                    picture = Picture("large_url", "medium_url", "thumbnail_url"),
                    nat = "US"
                )
            ),
            info = Info("seed", 1, 1, "1.0")
        )
        `when`(contactRemoteRepository.getContacts(10, 1)).thenReturn(remoteData)
        `when`(contactLocalRepository.clearAll()).thenReturn(Unit)

        // Act
        val result = cleanContactListUseCaseImpl.invoke().first()

        // Assert
        val expectedContacts = remoteData.results.mapIndexed { index, result ->
            ContactLocal(
                firstname = result.name.first,
                lastname = result.name.last,
                city = result.location.city,
                email = result.email,
                phone = result.phone,
                picture = result.picture.large,
                country = result.location.country,
                postcode = result.location.postcode,
                page = 1,
            )
        }.map { it.toContact() }
        assertEquals(expectedContacts, result)
    }
}
