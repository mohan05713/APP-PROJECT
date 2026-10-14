"""
EcoApartment OS - Primary Python Application Entry Point
Usage:
  python app.py          -> Starts the Full-Stack Pure Python Web & API App (http://localhost:8080)
  python app.py --cli    -> Launches Interactive Console / Terminal Mode
"""

import sys
import os

def main():
    args = sys.argv[1:]
    if "--cli" in args:
        import eco_apartment_python_app
        app = eco_apartment_python_app.EcoApartmentApp()
        app.run()
        return

    # Default mode: Launch the Pure Python Full-Stack Server
    import server
    server.start_server()

if __name__ == "__main__":
    main()
