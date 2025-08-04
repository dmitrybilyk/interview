package com.conduct.interview.auto;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecretServiceTest {

    @Mock
    private SecretStorageDao secretStorageDao;

    @InjectMocks
    private SecretService secretService;

    public

}