"""
EcoApartment OS - Universal Python Entry Point for Visual Studio / VS Code
Usage:
  python main.py       -> Runs the Pure Python Web App (Exact Mobile UI on http://localhost:8080)
  python main.py --cli -> Runs Interactive Terminal Mode
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

    # Default: Start the Full-Stack Pure Python Server with exact Mobile UI
    import server
    server.start_server()

if __name__ == "__main__":
    main()
