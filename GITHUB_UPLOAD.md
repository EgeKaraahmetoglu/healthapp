# GitHub Upload Instructions

Follow these step-by-step instructions to upload your Health App project to GitHub:

## Prerequisites

1. Create a GitHub account if you don't already have one
2. Install Git on your local machine if not already installed

## Steps to Upload

1. Open a terminal/command prompt and navigate to your project directory:

```bash
cd c:\Users\yenio\Desktop\healthapp_son
```

2. Initialize a new Git repository (if not already initialized):

```bash
git init
```

3. Add all files to Git staging:

```bash
git add .
```

4. Commit all changes with a descriptive message:

```bash
git commit -m "Initial commit of Health App"
```

5. Create a new repository on GitHub:
   - Go to https://github.com/new
   - Enter "healthapp" as the Repository name
   - Add a description (optional)
   - Choose between Public or Private repository
   - Do not initialize with README, .gitignore, or license
   - Click "Create repository"

6. Link your local repository to the GitHub repository:

```bash
git remote add origin https://github.com/yourusername/healthapp.git
```
(Replace "yourusername" with your actual GitHub username)

7. Push your code to GitHub:

```bash
git push -u origin main
```
or if you're using the older default branch name:
```bash
git push -u origin master
```

8. If prompted, enter your GitHub credentials to complete the upload process.

## Additional Commands

To update your repository after making changes:

```bash
git add .
git commit -m "Description of changes"
git push
```

To pull changes from the remote repository:

```bash
git pull
```

## Troubleshooting

If you encounter a "rejected non-fast-forward updates" error:
```bash
git pull --rebase origin main
git push origin main
```

If you're having authentication issues, consider setting up SSH or using a personal access token instead of password authentication.
