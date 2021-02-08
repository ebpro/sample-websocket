#!/bin/bash
docker run \
          --env GITHUBLOGIN="$GITHUBLOGIN" \
          --env GITHUBPASSWORD="$GITHUBPASSWORD" \
          --env SONAR_TOKEN="$SONAR_TOKEN" \
          --volume ~/.m2:/var/maven/.m2 \
          --volume ~/.sonar:/var/maven/.sonar \
          --volume ~/.config:/var/maven/.config \
          --volume ~/.ssh:/home/user/.ssh \
          --volume ~/.gitconfig:/home/user/.gitconfig \
          --volume "$(pwd)":/usr/src/mymaven \
          --workdir /usr/src/mymaven \
          --rm \
          --env PUID="$(id -u)" -e PGID="$(id -g)" \
          --env MAVEN_CONFIG=/var/maven/.m2 \
          brunoe/maven:3.6.3-jdk-11-openj9 \
          runuser --user user --group user -- mvn -B -e -T 1C -Duser.home=/var/maven --settings /usr/src/mymaven/.github/ci-settings.xml "$@"