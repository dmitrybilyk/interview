def readMyFile():
    try:
        with open("/home/dmytro/dev/projects/interview/python_playground/test.py", 'r') as f:
            return f.readlines()
    except FileNotFoundError:
        return []
    finally:
        print("something in finally")

def raiseBusinessException():
    amount = 10
    raise BusinessLogicError(f"Invalid amount: {amount}")

print(readMyFile())

if __name__ == "__main__":
    readMyFile()


class BusinessLogicError(Exception):
    """Exception related to Business"""
    pass

# try:
raiseBusinessException()
# except BusinessLogicError:
#     print("ups")