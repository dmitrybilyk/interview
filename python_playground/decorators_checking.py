from functools import wraps


def security_check(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        print("[Auth]: Validating JWT token...")
        print(f"args {args}")
        func(*args)  # Call the original function
        print("[Auth]: Validation complete.")
    return wrapper

@security_check
def get_user_data(d):
    print("Fetching data from DB...")

# Usage
get_user_data("dmytro")



def java_to_python(func):
    def wrapper(*args):
        print("Converting Java logic to Pythonic code...")
    return wrapper

@java_to_python
def myFunction(param1):
    print(f"Done {param1}")

myFunction("fff")